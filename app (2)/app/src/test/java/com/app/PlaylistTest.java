package com.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {

    private Playlist playlist;

    @BeforeEach
    void setUp() {
        playlist = new Playlist();
    }

    @Test
    void testAddTrack() {
        Track track = new Track("Song Title", "Artist Name", "path/to/song.mp3");
        playlist.addTrack(track);
        assertEquals(1, playlist.getTracks().size());
        assertEquals(track, playlist.getTracks().get(0));
    }

    @Test
    void testRemoveTrack() {
        Track track = new Track("Song Title", "Artist Name", "path/to/song.mp3");
        playlist.addTrack(track);
        playlist.removeTrack(track);
        assertEquals(0, playlist.getTracks().size());
    }

    @Test
    void testGetTracks() {
        Track track1 = new Track("Song Title 1", "Artist 1", "path/to/song1.mp3");
        Track track2 = new Track("Song Title 2", "Artist 2", "path/to/song2.mp3");
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        assertEquals(2, playlist.getTracks().size());
        assertTrue(playlist.getTracks().contains(track1));
        assertTrue(playlist.getTracks().contains(track2));
    }

    @Test
    void testClearPlaylist() {
        Track track = new Track("Song Title", "Artist Name", "path/to/song.mp3");
        playlist.addTrack(track);
        playlist.clear();
        assertEquals(0, playlist.getTracks().size());
    }
}