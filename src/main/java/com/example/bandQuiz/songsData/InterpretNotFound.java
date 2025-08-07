package com.example.bandQuiz.songsData;

public class InterpretNotFound extends Exception {
    public InterpretNotFound() {
        super("Interpret not found");
    }

    public InterpretNotFound(String message) {
        super(message);
    }

    public InterpretNotFound(String message, Throwable cause) {
        super(message,cause);
    }
}
