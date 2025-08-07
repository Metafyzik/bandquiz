package com.example.bandQuiz.songsData;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "scrapping")
@Component
public class ScrappingProperties {
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getRequestDelay() {
        return requestDelay;
    }

    public void setRequestDelay(int requestDelay) {
        this.requestDelay = requestDelay;
    }

    public int getRandomDelayUpperBound() {
        return randomDelayUpperBound;
    }

    public void setRandomDelayUpperBound(int randomDelayUpperBound) {
        this.randomDelayUpperBound = randomDelayUpperBound;
    }

    public int getMinimalLyricsLines() {
        return minimalLyricsLines;
    }

    public void setMinimalLyricsLines(int minimalLyricsLines) {
        this.minimalLyricsLines = minimalLyricsLines;
    }

    public int getMinimalSongs() {
        return minimalSongs;
    }

    public void setMinimalSongs(int minimalSongs) {
        this.minimalSongs = minimalSongs;
    }

    public String domain;
    public String userAgent;
    public int requestDelay; //TODO change to 800
    public int randomDelayUpperBound;
    public int minimalLyricsLines;
    public int minimalSongs;
}
