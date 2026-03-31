package com.app;

import javafx.util.Duration;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class LyricsParser {
    
    // Multiple patterns to handle different LRC formats
    private static final Pattern LRC_PATTERN_1 = Pattern.compile("\\[(\\d+):(\\d+)\\.(\\d+)\\](.*)");
    private static final Pattern LRC_PATTERN_2 = Pattern.compile("\\[(\\d+):(\\d+)\\](.*)");
    private static final Pattern LRC_PATTERN_3 = Pattern.compile("\\[(\\d+):(\\d+):(\\d+)\\](.*)");
    
    public static List<LyricsLine> parseLRC(File lrcFile) throws IOException {
        List<LyricsLine> lyrics = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(lrcFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                // Try different LRC patterns
                Matcher matcher = LRC_PATTERN_1.matcher(line);
                if (matcher.matches()) {
                    parseLRCWithMilliseconds(lyrics, matcher);
                    continue;
                }
                
                matcher = LRC_PATTERN_2.matcher(line);
                if (matcher.matches()) {
                    parseLRCWithoutMilliseconds(lyrics, matcher);
                    continue;
                }
                
                matcher = LRC_PATTERN_3.matcher(line);
                if (matcher.matches()) {
                    parseLRCWithHours(lyrics, matcher);
                    continue;
                }
                
                // If it doesn't match any pattern but starts with '[', it might be metadata
                if (line.startsWith("[")) {
                    continue; // Skip metadata lines like [ar:Artist]
                }
                
                // If line doesn't start with '[' and isn't empty, treat as text lyric with default timing
                if (!line.isEmpty()) {
                    // Assign default timing based on line number
                    Duration timestamp = Duration.seconds(lyrics.size() * 2.0); // 2 seconds per line
                    lyrics.add(new LyricsLine(timestamp, line));
                }
            }
        }
        
        // Sort by timestamp
        lyrics.sort(Comparator.comparing(LyricsLine::getTimestamp));
        
        // Debug output
        System.out.println("✓ Parsed " + lyrics.size() + " LRC lines from: " + lrcFile.getName());
        if (!lyrics.isEmpty()) {
            System.out.println("  First line: " + lyrics.get(0).getText() + " at " + 
                             formatDuration(lyrics.get(0).getTimestamp()));
            System.out.println("  Last line: " + lyrics.get(lyrics.size()-1).getText() + " at " + 
                             formatDuration(lyrics.get(lyrics.size()-1).getTimestamp()));
        }
        
        return lyrics;
    }
    
    private static void parseLRCWithMilliseconds(List<LyricsLine> lyrics, Matcher matcher) {
        int minutes = Integer.parseInt(matcher.group(1));
        int seconds = Integer.parseInt(matcher.group(2));
        int milliseconds = Integer.parseInt(matcher.group(3));
        
        // Handle different millisecond formats
        if (matcher.group(3).length() == 2) {
            // Format [mm:ss.xx] where xx is hundredths of a second
            milliseconds = milliseconds * 10;
        } else if (matcher.group(3).length() == 1) {
            // Format [mm:ss.x] where x is tenths of a second
            milliseconds = milliseconds * 100;
        }
        
        Duration timestamp = Duration.seconds(minutes * 60 + seconds)
                                    .add(Duration.millis(milliseconds));
        String text = matcher.group(4).trim();
        
        if (!text.isEmpty()) {
            lyrics.add(new LyricsLine(timestamp, text));
        }
    }
    
    private static void parseLRCWithoutMilliseconds(List<LyricsLine> lyrics, Matcher matcher) {
        int minutes = Integer.parseInt(matcher.group(1));
        int seconds = Integer.parseInt(matcher.group(2));
        
        Duration timestamp = Duration.seconds(minutes * 60 + seconds);
        String text = matcher.group(3).trim();
        
        if (!text.isEmpty()) {
            lyrics.add(new LyricsLine(timestamp, text));
        }
    }
    
    private static void parseLRCWithHours(List<LyricsLine> lyrics, Matcher matcher) {
        int hours = Integer.parseInt(matcher.group(1));
        int minutes = Integer.parseInt(matcher.group(2));
        int seconds = Integer.parseInt(matcher.group(3));
        
        Duration timestamp = Duration.seconds(hours * 3600 + minutes * 60 + seconds);
        String text = matcher.group(4).trim();
        
        if (!text.isEmpty()) {
            lyrics.add(new LyricsLine(timestamp, text));
        }
    }
    
    public static List<LyricsLine> parseText(File textFile) throws IOException {
        List<LyricsLine> lyrics = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // Assign 3 seconds per line for plain text
                    Duration timestamp = Duration.seconds(lineNumber * 3.0);
                    lyrics.add(new LyricsLine(timestamp, line));
                    lineNumber++;
                }
            }
        }
        
        // Debug output
        System.out.println("✓ Parsed " + lyrics.size() + " TXT lines from: " + textFile.getName());
        if (!lyrics.isEmpty()) {
            System.out.println("  First line: " + lyrics.get(0).getText() + " at " + 
                             formatDuration(lyrics.get(0).getTimestamp()));
            System.out.println("  Last line: " + lyrics.get(lyrics.size()-1).getText() + " at " + 
                             formatDuration(lyrics.get(lyrics.size()-1).getTimestamp()));
        }
        
        return lyrics;
    }
    
    public static List<LyricsLine> parseLyricsFile(File lyricsFile) throws IOException {
        String filename = lyricsFile.getName().toLowerCase();
        System.out.println("📄 Parsing lyrics file: " + filename);
        
        if (filename.endsWith(".lrc")) {
            return parseLRC(lyricsFile);
        } else if (filename.endsWith(".txt")) {
            return parseText(lyricsFile);
        } else {
            System.out.println("✗ Unsupported format: " + filename);
            throw new IllegalArgumentException("Unsupported lyrics format: " + filename);
        }
    }
    
    public static String getCurrentLyric(List<LyricsLine> lyrics, Duration currentTime) {
        if (lyrics == null || lyrics.isEmpty()) {
            return "No lyrics loaded";
        }
        
        // If time is before first lyric, show first lyric
        if (currentTime.lessThan(lyrics.get(0).getTimestamp())) {
            return lyrics.get(0).getText();
        }
        
        // If time is after last lyric, show last lyric
        if (currentTime.greaterThanOrEqualTo(lyrics.get(lyrics.size()-1).getTimestamp())) {
            return lyrics.get(lyrics.size()-1).getText();
        }
        
        // Find the current lyric
        for (int i = 0; i < lyrics.size(); i++) {
            LyricsLine currentLine = lyrics.get(i);
            
            // Get next lyric's timestamp (or null if this is the last lyric)
            Duration nextTimestamp = (i < lyrics.size() - 1) ? 
                lyrics.get(i + 1).getTimestamp() : null;
            
            // Check if current time is between this lyric and the next
            boolean isAtOrAfterThisLyric = currentTime.greaterThanOrEqualTo(currentLine.getTimestamp());
            boolean isBeforeNextLyric = (nextTimestamp == null) || currentTime.lessThan(nextTimestamp);
            
            if (isAtOrAfterThisLyric && isBeforeNextLyric) {
                return currentLine.getText();
            }
        }
        
        // Fallback: show the lyric with timestamp closest to current time
        LyricsLine closest = lyrics.get(0);
        double closestDiff = Math.abs(currentTime.toSeconds() - closest.getTimestamp().toSeconds());
        
        for (LyricsLine line : lyrics) {
            double diff = Math.abs(currentTime.toSeconds() - line.getTimestamp().toSeconds());
            if (diff < closestDiff) {
                closest = line;
                closestDiff = diff;
            }
        }
        
        return closest.getText();
    }
    
    public static List<String> getUpcomingLyrics(List<LyricsLine> lyrics, Duration currentTime, int count) {
        List<String> upcoming = new ArrayList<>();
        if (lyrics == null || lyrics.isEmpty()) return upcoming;
        
        // Find the index of the current lyric
        int currentIndex = -1;
        for (int i = 0; i < lyrics.size(); i++) {
            if (lyrics.get(i).getTimestamp().greaterThan(currentTime)) {
                currentIndex = i;
                break;
            }
        }
        
        // If we didn't find a lyric after current time, we're at the end
        if (currentIndex == -1) {
            return upcoming;
        }
        
        // Get the next 'count' lyrics
        int endIndex = Math.min(currentIndex + count, lyrics.size());
        for (int i = currentIndex; i < endIndex; i++) {
            upcoming.add(lyrics.get(i).getText());
        }
        
        return upcoming;
    }
    
    private static String formatDuration(Duration duration) {
        int totalSeconds = (int) duration.toSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        int millis = (int) (duration.toMillis() % 1000);
        
        if (millis > 0) {
            return String.format("%02d:%02d.%03d", minutes, seconds, millis);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}