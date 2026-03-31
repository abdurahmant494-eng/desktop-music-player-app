package com.app;

import javafx.util.Duration;

public class LyricsLine {
    private final Duration timestamp;
    private final String text;
    
    public LyricsLine(Duration timestamp, String text) {
        this.timestamp = timestamp;
        this.text = text;
    }
    
    public Duration getTimestamp() {
        return timestamp;
    }
    
    public String getText() {
        return text;
    }
    
    public boolean isBefore(Duration time) {
        return timestamp.lessThanOrEqualTo(time);
    }
    
    public boolean isAfter(Duration time) {
        return timestamp.greaterThan(time);
    }
    
    @Override
    public String toString() {
        int totalSeconds = (int) timestamp.toSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        int millis = (int) (timestamp.toMillis() % 1000);
        
        return String.format("[%02d:%02d.%03d] %s", 
            minutes, seconds, millis, text);
    }
}