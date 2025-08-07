package com.example.bandQuiz.songsData;

public class NotEnoughLyrics extends Exception {
    public NotEnoughLyrics() {
        super("Not enough song with lyrics found the interpret");
    }

    public NotEnoughLyrics(String message) {
        super(message);
    }

    public NotEnoughLyrics(String message, Throwable cause) {
        super(message,cause);
    }
}
