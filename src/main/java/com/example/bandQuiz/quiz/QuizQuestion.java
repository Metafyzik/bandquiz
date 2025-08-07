package com.example.bandQuiz.quiz;

import java.util.List;
import java.util.Objects;

public record QuizQuestion(List<String> lines, List<String> answers, String correctAnswer)
{
    public QuizQuestion {
        Objects.requireNonNull(lines, "Lines cannot be null");
        Objects.requireNonNull(answers, "Answers cannot be null");
        Objects.requireNonNull(correctAnswer, "Correct answer cannot be null");
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Lines cannot be empty");
        }
        if (answers.isEmpty()) {
            throw new IllegalArgumentException("Answers cannot be empty");
        }
        if (!answers.contains(correctAnswer)) {
            throw new IllegalArgumentException("Correct answer must be one of the provided answers");
        }
        lines = List.copyOf(lines); // Ensure immutability
        answers = List.copyOf(answers);
    }
}
