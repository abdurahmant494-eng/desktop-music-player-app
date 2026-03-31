package com.app;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private List<Track> tracks;

    public Playlist() {
        this.tracks = new ArrayList<>();
    }

    public void addTrack(Track track) {
        tracks.add(track);
    }

    public void removeTrack(Track track) {
        tracks.remove(track);
    }

    public List<Track> getTracks() {
        return new ArrayList<>(tracks);
    }

    public void clear() {
        tracks.clear();
    }

    public Track getTrack(int index) {
        if (index < 0 || index >= tracks.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + tracks.size());
        }
        return tracks.get(index);
    }

    public int size() {
        return tracks.size();
    }
}