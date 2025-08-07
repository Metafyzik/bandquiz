package com.example.bandQuiz.songsData;


//general exception for something not working during fetching songs data
//be it not functioning site, exception during scraping (html is different) and so
public class ExceptionDuringDataSongFetch extends Exception {
    public ExceptionDuringDataSongFetch() {
        super("Exception during fetching of data");
    }

    public ExceptionDuringDataSongFetch(String message) {
        super(message);
    }

    public ExceptionDuringDataSongFetch(String message, Exception e) {
        super(message, e);
    }
}
