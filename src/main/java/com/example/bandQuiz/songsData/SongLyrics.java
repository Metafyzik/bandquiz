package com.example.bandQuiz.songsData;

import java.util.List;

public record SongLyrics(String songTitle, List<String> lyrics) {

    @Override
    public String toString() {
        return "SongLyrics{songTitle='" + songTitle + "', lyrics=" + lyrics + "}";
    }
}
