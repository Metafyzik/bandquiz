package com.example.bandQuiz.songsData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Service
public class SongsDataKaraokeTexty implements SongsData  {
    private static final Logger logger = LogManager.getLogger(SongsDataKaraokeTexty.class);

    private final ScrappingProperties scrappingProperties;

    public SongsDataKaraokeTexty(ScrappingProperties scrappingProperties) {
        this.scrappingProperties = scrappingProperties;
    }

    public Document searchInterpret(String interpretName) throws ExceptionDuringDataSongFetch {
        try {
            //search the interpret on main page
            Document searchResults = Jsoup.connect(scrappingProperties.getDomain() + "/search")
                    .data("sid", "mxybv")  // Hidden field value
                    .data("q", interpretName) // Search query
                    .userAgent(scrappingProperties.getUserAgent())
                    .post(); // Using POST since it's a form submission

            respectfulDelay(); // Add delay after request
            return searchResults;
        } catch (IOException e) {
            logger.error("IOException during searchInterpret()", e);
            throw new ExceptionDuringDataSongFetch();
        }
    }

    public Elements findTopTenSongsItems(Document interpretPage) throws ExceptionDuringDataSongFetch {
        // Select all <a> elements inside <li> inside <ul class="top_songs">
        Elements songsItems = interpretPage.select("ul.top_songs li a");

        if (songsItems.isEmpty()) {
            ExceptionDuringDataSongFetch exception = new ExceptionDuringDataSongFetch();
            logger.error("songsItem are empty",exception);
            throw exception;
        }
        return songsItems;
    }
    public List<String> getSongLyrics(Document songPage) throws ExceptionDuringDataSongFetch {
        Elements rawLyrics;
        if (!songPage.select("span.para_col1").isEmpty()) {
            rawLyrics = songPage.select("span.para_col1");
        } else if (!songPage.select("span.para_1lyrics_col1").isEmpty()){
            rawLyrics = songPage.select("span.para_1lyrics_col1");
        } else{
            ExceptionDuringDataSongFetch exception = new ExceptionDuringDataSongFetch();
            logger.error("rawLyrics are empty",exception);
            throw exception;
        }

        List<String> lyrics = new ArrayList<>();
        for (Element stanza : rawLyrics) {
            String text = replaceChars(stanza);

            //ignore close div element mixed lyrics
            if (text.contains("div class")) {continue;}

            lyrics.addAll(
                    Arrays.stream(text.split("\n"))
                    .map(line -> line.trim())
                    .filter(line -> !line.isEmpty()) //erase empty lines
                    .filter(line -> !line.matches("^[^a-zA-Z]+$")) //only lines that contains letters
                    .toList()
            );
        }

        if (lyrics.isEmpty()) {
            ExceptionDuringDataSongFetch exception = new ExceptionDuringDataSongFetch();
            logger.error("lyrics are empty", exception);
            throw exception;
        }

        return lyrics;
    }

    public String replaceChars(Element stanza) {
        //Clean <br>
        String text = stanza.html().replaceAll("(?i)<br\\s*/?>", "").trim();
        //clean "["...text.."]"
        return text.replaceAll("\\[[^\\]]*\\]","");
    }

    public Element findInterpretInList(Document searchResults, String interpretName) throws InterpretNotFound{
        //find the matching interpret "Interpreti" list
        for(Element result : searchResults.select(".searchsresrow_artists a")){

            if (result.text().equalsIgnoreCase(interpretName) ) {
                return result;
            }
        }

        InterpretNotFound exception = new InterpretNotFound();
        logger.error("Failed to find interpret: {}", interpretName, exception);
        throw exception;
    }

    public Document goToInterpret(Document searchResults, String interpretName) throws InterpretNotFound, ExceptionDuringDataSongFetch {
        Element interpretItem = findInterpretInList(searchResults,interpretName);

        String relativeUrl = interpretItem.attr("href");
        String fullUrl = scrappingProperties.getDomain() + relativeUrl;

        return loadToPage(fullUrl);
    }

    public Document loadToPage(String fullUrl) throws ExceptionDuringDataSongFetch {
        try {
            Document interpretPage = Jsoup.connect(fullUrl)
                    .userAgent(scrappingProperties.getUserAgent())
                    .get();

            return interpretPage;
        } catch (IOException e) {
            logger.error("IOException during searchInterpret()", e);
            throw new ExceptionDuringDataSongFetch();
        }
    }

    @Override
    public List<SongLyrics> fetchSongsData(String interpretName) throws InterpretNotFound, ExceptionDuringDataSongFetch, NotEnoughLyrics {
        validateInsertedInterpret(interpretName);

        Document searchResultsPage = searchInterpret(interpretName);
        Document interpretPage = goToInterpret(searchResultsPage, interpretName);

        Elements topTenSongsItems = findTopTenSongsItems(interpretPage);

        List<String> topTenSongs = topTenSongsItems.stream().map(s -> s.text()).toList();
        List<String> topTenSongURL = topTenSongsItems.stream().map(l -> scrappingProperties.getDomain() + l.attr("href")).toList();

        int songAmount = topTenSongs.size();
        List<SongLyrics> songsAndLyrics = new ArrayList<>();

        for (int i = 0; i < songAmount; i++) {
            Document songPage = loadToPage(topTenSongURL.get(i));

            if (i+1 < songAmount) {respectfulDelay();}

            List<String> lyrics =  getSongLyrics(songPage);

            if(hasSongEnoughLyrics(lyrics.size(), scrappingProperties.getMinimalLyricsLines())){
                songsAndLyrics.add(new SongLyrics(topTenSongs.get(i),getSongLyrics(songPage)) );
            }
        }

        //does interpret have enough lyrics
        if(songsAndLyrics.size() < scrappingProperties.getMinimalSongs()){
            NotEnoughLyrics exception = new NotEnoughLyrics();
            logger.error("The interpret does not have enough lyrics", exception);
            throw exception;
        }

        return songsAndLyrics;
    }

    public void validateInsertedInterpret(String interpretName) {
        if (interpretName == null || interpretName.trim().isEmpty()) {
            throw new IllegalArgumentException("Interpret name cannot be null or empty");
        }
    }

    private void respectfulDelay() throws ExceptionDuringDataSongFetch {
        try {
            // Vary the delay slightly to appear more human-like
            int delay = scrappingProperties.getRequestDelay() + new Random().nextInt(scrappingProperties.getRandomDelayUpperBound());
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("HTTP request failed", e);
            throw new ExceptionDuringDataSongFetch();
        }
    }
    public boolean hasSongEnoughLyrics(int lyrics, int requiredLines) {
        return (lyrics >= requiredLines);
    }
}
