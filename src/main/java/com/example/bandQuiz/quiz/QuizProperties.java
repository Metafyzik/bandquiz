package com.example.bandQuiz.quiz;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "quiz")
@Component
public class QuizProperties {
    public int getNumQuizQuestionsAmount() {
        return numQuizQuestionsAmount;
    }

    public void setNumQuizQuestionsAmount(int numQuizQuestionsAmount) {
        this.numQuizQuestionsAmount = numQuizQuestionsAmount;
    }

    public int getNumSongLines() {
        return numSongLines;
    }

    public void setNumSongLines(int numSongLines) {
        this.numSongLines = numSongLines;
    }

    private int numQuizQuestionsAmount;
    private int numSongLines;
}