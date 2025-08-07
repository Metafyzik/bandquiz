package com.example.bandQuiz.quiz;

import com.example.bandQuiz.songsData.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizInitializer {

    private static final Logger logger = LogManager.getLogger(QuizInitializer.class);

    private final SongsData songsData;
    private final GuessLyricsQuiz quiz;

    public QuizInitializer(SongsData songsData, GuessLyricsQuiz quiz) {
        this.songsData = songsData;
        this.quiz = quiz;
    }

    public List<QuizQuestion> generateQuiz(String interpretName) throws ExceptionDuringDataSongFetch, InterpretNotFound, NotEnoughLyrics {
        List<SongLyrics> songsLyrics = songsData.fetchSongsData(interpretName);

        logger.info("Fetched {} songs for artist: {}", songsLyrics.size(), interpretName);
        return quiz.createQuiz(songsLyrics);
    }

    public int checkAnswers(List<QuizQuestion> questions, Map<String, String> answers){
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            String key = "q" + (i + 1);
            String userAnswer = answers.get(key);
            if (questions.get(i).correctAnswer().equalsIgnoreCase(userAnswer)) {
                score++;
            }
        }
        return score;
    }
}