package com.example.bandQuiz.songsData;

import java.util.List;

public interface SongsData {

    List<SongLyrics> fetchSongsData(String artistName) throws InterpretNotFound, ExceptionDuringDataSongFetch, NotEnoughLyrics;
}
