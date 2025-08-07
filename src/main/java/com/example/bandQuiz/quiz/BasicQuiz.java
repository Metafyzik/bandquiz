package com.example.bandQuiz.quiz;

import com.example.bandQuiz.songsData.SongLyrics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Service
public class BasicQuiz implements GuessLyricsQuiz{
    private final QuizProperties properties;

    public BasicQuiz(QuizProperties properties) {
        this.properties = properties;
    }


    @Override
    public List<QuizQuestion> createQuiz(List<SongLyrics> songData) {

        List<SongLyrics> quizRandomSongs = getRandomSongs(songData,properties.getNumQuizQuestionsAmount());
        List<QuizQuestion> quizQuestions = getQuizQuestions(quizRandomSongs, songData);

        return quizQuestions;
    }

    public List<QuizQuestion> getQuizQuestions(List<SongLyrics> quizSongs, List<SongLyrics> songData) {
        List<QuizQuestion> quizQuestions = new ArrayList<>();

        for (SongLyrics songLyrics : quizSongs) {
            List<String> lyricsLines = randomLyrics(songLyrics, properties.getNumSongLines());
            String correctAnswer = songLyrics.songTitle();

            //exclude correct answer
            List<SongLyrics> temp = new ArrayList<>(songData.stream().filter(s -> s != songLyrics).toList());

            //pick three random other song
            Collections.shuffle(temp);
            List<String> answers = new ArrayList<>(temp.stream().map(SongLyrics::songTitle).toList().subList(0, 3));

            answers.add(correctAnswer);
            //make the correct answer appear on a random position
            Collections.shuffle(answers);

            QuizQuestion quizQuestion = new QuizQuestion(lyricsLines, answers, correctAnswer);
            quizQuestions.add(quizQuestion);
        }
        return quizQuestions;
    }

    public List<String> randomLyrics(SongLyrics songLyrics, int songsLines) {
        String songTitle = songLyrics.songTitle();

        //get rid of lines containing song's title
        List<String> cleanedLyrics = songLyrics.lyrics().stream().filter(l -> !l.toLowerCase().contains(songTitle.toLowerCase())).toList();

        int randomFirst = ThreadLocalRandom.current().nextInt(cleanedLyrics.size() - songsLines);

        List<String> lyricsLines = IntStream.range(0, songsLines)
                .mapToObj(i -> cleanedLyrics.get(i + randomFirst))
                .toList();

        return lyricsLines;
    }

    public List<SongLyrics> getRandomSongs(List<SongLyrics> songData, int quizQuestionsAmount) {
        // Shuffle the list to randomize the order
        Collections.shuffle(songData);

        if (quizQuestionsAmount > songData.size()) {
            throw new IllegalArgumentException("Cannot pick more items than are in the list.");
        }

        return songData.subList(0, quizQuestionsAmount);
    }
}

