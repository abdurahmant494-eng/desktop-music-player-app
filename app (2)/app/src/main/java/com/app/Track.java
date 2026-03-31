package com.app;

import java.util.Objects;

public class Track {
    private final String title;
    private final String artist;
    private final String filePath;

    // Old constructor kept for compatibility: (path, title)
    public Track(String path, String title) {
        this.title = title != null ? title : new java.io.File(path).getName();
        this.artist = null;
        this.filePath = path;
    }

    // Preferred constructor used in tests: (title, artist, filePath)
    public Track(String title, String artist, String filePath) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
    }

    public String getPath() { return filePath; }
    public String getFilePath() { return filePath; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }

    @Override
    public String toString() { return title; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Objects.equals(title, track.title) && Objects.equals(filePath, track.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, filePath);
    }
}