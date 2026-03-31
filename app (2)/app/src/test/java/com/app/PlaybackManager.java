package com.app;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlaybackManager {
    private final List<String> tracks = new ArrayList<>();
    private MediaPlayer player;
    private int currentIndex = -1;

    public void addTrack(String filePath) {
        if (filePath == null || filePath.isEmpty()) return;
        tracks.add(filePath);
    }

    // Overloaded method for Track objects (for compatibility with tests)
    public void addTrack(Track track) {
        if (track != null && track.getFilePath() != null) {
            tracks.add(track.getFilePath());
        }
    }

    public void play(String trackName) {
        for (int i = 0; i < tracks.size(); i++) {
            File file = new File(tracks.get(i));
            if (file.getName().equals(trackName)) {
                play(i);
                return;
            }
        }
        
        try {
            int index = Integer.parseInt(trackName);
            play(index);
        } catch (NumberFormatException e) {
            // If no track found and we have a player, resume
            if (player != null && player.getStatus() == MediaPlayer.Status.PAUSED) {
                player.play();
            }
        }
    }

    // Add this method to fix the error
    public void play() {
        if (player != null && player.getStatus() == MediaPlayer.Status.PAUSED) {
            player.play();
        } else if (currentIndex >= 0 && currentIndex < tracks.size()) {
            play(currentIndex);
        } else if (!tracks.isEmpty()) {
            play(0);
        }
    }

    public void play(int index) {
        if (index < 0 || index >= tracks.size()) return;
        
        if (player != null) {
            player.stop();
            player.dispose();
        }
        
        try {
            String filePath = tracks.get(index);
            Media media = new Media(new File(filePath).toURI().toString());
            player = new MediaPlayer(media);
            currentIndex = index;
            
            player.setOnEndOfMedia(() -> {
                int next = currentIndex + 1;
                if (next < tracks.size()) {
                    play(next);
                }
            });
            
            player.play();
            
        } catch (Exception e) {
            System.err.println("Error playing track: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
            currentIndex = -1;
        }
    }

    public void setVolume(double volume) {
        if (player != null) {
            player.setVolume(Math.max(0, Math.min(1.0, volume)));
        }
    }

    public void seek(double progress) {
        if (player != null && player.getTotalDuration() != null) {
            Duration total = player.getTotalDuration();
            Duration seekTime = total.multiply(progress);
            player.seek(seekTime);
        }
    }

    // Add nextTrack method for compatibility
    public void nextTrack() {
        if (tracks.isEmpty()) return;
        
        int next = currentIndex + 1;
        if (next >= tracks.size()) {
            next = 0;
        }
        play(next);
    }

    // Add previousTrack method for compatibility
    public void previousTrack() {
        if (tracks.isEmpty()) return;
        
        int prev = currentIndex - 1;
        if (prev < 0) {
            prev = tracks.size() - 1;
        }
        play(prev);
    }

    public String getCurrentTrack() {
        if (currentIndex >= 0 && currentIndex < tracks.size()) {
            File file = new File(tracks.get(currentIndex));
            return file.getName();
        }
        return null;
    }

    public String getCurrentTimeFormatted() {
        if (player != null && player.getCurrentTime() != null) {
            Duration currentTime = player.getCurrentTime();
            int minutes = (int) currentTime.toMinutes();
            int seconds = (int) currentTime.toSeconds() % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
        return "00:00";
    }

    public String getTotalTimeFormatted() {
        if (player != null && player.getTotalDuration() != null) {
            Duration totalTime = player.getTotalDuration();
            int minutes = (int) totalTime.toMinutes();
            int seconds = (int) totalTime.toSeconds() % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
        return "00:00";
    }

    public double getCurrentProgress() {
        if (player != null && player.getCurrentTime() != null && player.getTotalDuration() != null) {
            Duration current = player.getCurrentTime();
            Duration total = player.getTotalDuration();
            
            if (total.greaterThan(Duration.ZERO)) {
                return current.toMillis() / total.toMillis();
            }
        }
        return 0.0;
    }

    public boolean isPlaying() {
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }
}