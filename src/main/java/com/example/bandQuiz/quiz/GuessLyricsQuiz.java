package com.example.bandQuiz.quiz;

import com.example.bandQuiz.songsData.SongLyrics;

import java.util.List;

public interface GuessLyricsQuiz {
    public List<QuizQuestion> createQuiz(List<SongLyrics> songData);

}
