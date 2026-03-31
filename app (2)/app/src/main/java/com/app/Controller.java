package com.app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.input.MouseEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.text.Text;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Controller {

    @FXML
    private ListView<String> playlistView;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button prevButton;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider progressSlider;
    @FXML
    private Label currentTrackLabel;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private ImageView albumArtView;
    
    // Lyrics display components
    @FXML
    private Label currentLyricLabel;
    @FXML
    private Label lyricsStatusLabel;
    @FXML
    private VBox upcomingLyricsBox;
    
    private PlaybackManager playbackManager;
    private Stage mainWindow;
    private Timeline timeline;
    private Map<String, String> filePaths = new HashMap<>();
    private boolean isSeeking = false;
    private boolean isPlaying = false;

    // Lyrics data
    private List<LyricsLine> currentLyrics = new ArrayList<>();
    private String currentAudioFile = "";

    public void initialize() {
        System.out.println("========================================");
        System.out.println("🎵 Modern Music Player v1.0");
        System.out.println("Developed by: Tahir Abduro");
        System.out.println("Portfolio: https://tahir-abduro.netlify.app/");
        System.out.println("========================================");
        
        playbackManager = new PlaybackManager();
        
        // Setup volume slider
        volumeSlider.setValue(50);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            playbackManager.setVolume(newVal.doubleValue() / 100.0);
        });
        
        // Setup progress slider
        progressSlider.setValue(0);
        
        // Setup album art view
        setupAlbumArt();
        
        // Setup lyrics display
        setupLyricsDisplay();
        
        // Set initial button symbols
        updateButtonSymbols();
        
        // Track selection listener
        playlistView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentTrackLabel.setText("Now Playing: " + newValue);
                updateAlbumArt(newValue);
                
                // Load lyrics for the selected track
                String filePath = filePaths.get(newValue);
                if (filePath != null) {
                    loadLyricsForTrack(filePath);
                }
            } else {
                currentTrackLabel.setText("Now Playing: ");
                lyricsStatusLabel.setText("Select a track to load lyrics");
            }
        });
        
        // Double-click to play track
        playlistView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double click
                String selectedTrack = playlistView.getSelectionModel().getSelectedItem();
                if (selectedTrack != null) {
                    System.out.println("🎵 Double-clicked on track: " + selectedTrack);
                    playbackManager.play(selectedTrack);
                    isPlaying = true;
                    updateButtonSymbols();
                    
                    // Ensure lyrics are loaded for this track
                    String filePath = filePaths.get(selectedTrack);
                    if (filePath != null && !filePath.equals(currentAudioFile)) {
                        loadLyricsForTrack(filePath);
                    }
                }
            }
        });
        
        // Setup timeline for updating time display, progress, and lyrics
        timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> updateProgress()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        System.out.println("=== Controller Initialization Complete ===");
    }

    public void setMainWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;
    }

    @FXML
    private void handlePlay() {
        String selectedTrack = playlistView.getSelectionModel().getSelectedItem();
        if (selectedTrack != null) {
            System.out.println("▶ Playing selected track: " + selectedTrack);
            playbackManager.play(selectedTrack);
            isPlaying = true;
            updateButtonSymbols();
        } else if (!playlistView.getItems().isEmpty()) {
            playlistView.getSelectionModel().select(0);
            String firstTrack = playlistView.getItems().get(0);
            System.out.println("▶ Playing first track: " + firstTrack);
            playbackManager.play(firstTrack);
            isPlaying = true;
            updateButtonSymbols();
        } else {
            System.out.println("▶ Resuming playback");
            playbackManager.play();
            isPlaying = true;
            updateButtonSymbols();
        }
    }

    @FXML
    private void handlePause() {
        System.out.println("⏸ Pausing playback");
        playbackManager.pause();
        isPlaying = false;
        updateButtonSymbols();
    }

    @FXML
    private void handleStop() {
        System.out.println("⏹ Stopping playback");
        playbackManager.stop();
        progressSlider.setValue(0);
        currentTimeLabel.setText("00:00");
        isPlaying = false;
        updateButtonSymbols();
        
        // Reset lyrics display when stopped
        if (!currentLyrics.isEmpty()) {
            currentLyricLabel.setText(currentLyrics.get(0).getText());
            updateUpcomingLyrics();
        }
    }

    @FXML
    private void handleNext() {
        int itemCount = playlistView.getItems().size();
        if (itemCount == 0) {
            System.out.println("⏭ Next: Playlist is empty");
            return;
        }
        
        int currentIndex = playlistView.getSelectionModel().getSelectedIndex();
        int nextIndex;
        if (currentIndex == -1) {
            nextIndex = 0;
        } else {
            nextIndex = (currentIndex + 1) % itemCount;
        }
        
        String nextTrack = playlistView.getItems().get(nextIndex);
        System.out.println("⏭ Next track: " + nextTrack);
        
        playlistView.getSelectionModel().select(nextIndex);
        playbackManager.play(nextTrack);
        isPlaying = true;
        updateButtonSymbols();
    }

    @FXML
    private void handlePrevious() {
        int itemCount = playlistView.getItems().size();
        if (itemCount == 0) {
            System.out.println("⏮ Previous: Playlist is empty");
            return;
        }
        
        int currentIndex = playlistView.getSelectionModel().getSelectedIndex();
        int prevIndex;
        if (currentIndex == -1) {
            prevIndex = 0;
        } else {
            prevIndex = (currentIndex - 1 + itemCount) % itemCount;
        }
        
        String prevTrack = playlistView.getItems().get(prevIndex);
        System.out.println("⏮ Previous track: " + prevTrack);
        
        playlistView.getSelectionModel().select(prevIndex);
        playbackManager.play(prevTrack);
        isPlaying = true;
        updateButtonSymbols();
    }

    @FXML
    private void handleVolumeChange(MouseEvent event) {
        playbackManager.setVolume(volumeSlider.getValue() / 100.0);
    }

    @FXML
    private void handleProgressMousePressed(MouseEvent event) {
        isSeeking = true;
    }

    @FXML
    private void handleProgressMouseReleased(MouseEvent event) {
        if (isSeeking && playbackManager.getCurrentTrack() != null) {
            double progress = progressSlider.getValue() / 100.0;
            System.out.println("⏩ Seeking to: " + (progress * 100) + "%");
            playbackManager.seek(progress);
        }
        isSeeking = false;
    }

    @FXML
    private void handleProgressDrag(MouseEvent event) {
        event.consume();
    }

    @FXML
    private void handleOpen() {
        System.out.println("📂 Opening file dialog...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Audio File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.aac", "*.flac", "*.m4a", "*.ogg"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        List<File> files = fileChooser.showOpenMultipleDialog(mainWindow);
        if (files != null) {
            System.out.println("📂 Selected " + files.size() + " file(s)");
            for (File file : files) {
                String fileName = file.getName();
                String filePath = file.getAbsolutePath();
                
                filePaths.put(fileName, filePath);
                playlistView.getItems().add(fileName);
                playbackManager.addTrack(filePath);
                System.out.println("  Added: " + fileName);
            }
            
            if (playlistView.getItems().size() == 1) {
                playlistView.getSelectionModel().select(0);
                updateAlbumArt(playlistView.getItems().get(0));
                
                // Load lyrics for the first track
                String firstTrack = playlistView.getItems().get(0);
                String filePath = filePaths.get(firstTrack);
                if (filePath != null) {
                    loadLyricsForTrack(filePath);
                }
            }
        } else {
            System.out.println("📂 No files selected");
        }
    }

    @FXML
    private void handleLoadLyrics() {
        System.out.println("📄 Loading lyrics file...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Lyrics File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Lyrics Files", "*.lrc", "*.txt"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File lyricsFile = fileChooser.showOpenDialog(mainWindow);
        if (lyricsFile != null) {
            try {
                currentLyrics = LyricsParser.parseLyricsFile(lyricsFile);
                System.out.println("✓ Manually loaded " + currentLyrics.size() + " lyrics lines");
                lyricsStatusLabel.setText("Lyrics: " + lyricsFile.getName() + " (" + currentLyrics.size() + " lines)");
                updateLyricsDisplay();
            } catch (Exception e) {
                System.out.println("✗ Error loading lyrics: " + e.getMessage());
                lyricsStatusLabel.setText("Error: " + e.getMessage());
                currentLyricLabel.setText("Lyrics error - check console");
            }
        } else {
            System.out.println("📄 No lyrics file selected");
        }
    }
    
    @FXML
    private void showAboutDialog() {
        Alert aboutDialog = new Alert(Alert.AlertType.INFORMATION);
        aboutDialog.setTitle("About Music Player");
        aboutDialog.setHeaderText("🎵 Modern Music Player v1.0");
        
        String aboutText = """
            A professional music player with lyrics synchronization 
            and glass morphism UI.
            
            Features:
            • Audio playback (MP3, WAV, AAC, FLAC, M4A, OGG)
            • Lyrics synchronization (LRC/TXT format)
            • Glass morphism UI design
            • Playlist management
            • Dynamic album art generation
            
            Developer Information:
            Name: Tahir Abduro
            Email: tahirabduro@gmail.com
            Location: Addis Ababa, Ethiopia
            
            Technologies Used:
            • JavaFX 21
            • Maven
            • CSS3 for styling
            • Java Media API
            
            © 2024 Tahir Abduro. All rights reserved.
            """;
        
        // Create clickable hyperlink
        Hyperlink portfolioLink = new Hyperlink("https://tahir-abduro.netlify.app/");
        portfolioLink.setOnAction(e -> openPortfolio());
        portfolioLink.setStyle("-fx-text-fill: #4facfe; -fx-font-size: 14px;");
        
        // Create contact info
        Hyperlink emailLink = new Hyperlink("tahirabduro@gmail.com");
        emailLink.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().mail(
                    new java.net.URI("mailto:tahirabduro@gmail.com?subject=Music%20Player%20Inquiry")
                );
            } catch (Exception ex) {
                System.out.println("Could not open email: " + ex.getMessage());
            }
        });
        emailLink.setStyle("-fx-text-fill: #4facfe; -fx-font-size: 14px;");
        
        VBox content = new VBox(10,
            new Text(aboutText),
            new HBox(5, new Label("🌐 Portfolio: "), portfolioLink),
            new HBox(5, new Label("📧 Email: "), emailLink),
            new Label("\nThank you for using my application!")
        );
        content.setPadding(new Insets(10));
        
        aboutDialog.getDialogPane().setContent(content);
        aboutDialog.getDialogPane().setPrefSize(500, 400);
        aboutDialog.showAndWait();
    }
    
    @FXML
    private void openPortfolio() {
        try {
            System.out.println("🌐 Opening portfolio: https://tahir-abduro.netlify.app/");
            java.awt.Desktop.getDesktop().browse(
                new java.net.URI("https://tahir-abduro.netlify.app/")
            );
        } catch (Exception e) {
            System.out.println("✗ Could not open portfolio: " + e.getMessage());
            // Fallback: Show URL in dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Portfolio - Tahir Abduro");
            alert.setHeaderText("My Portfolio Website");
            alert.setContentText("Visit: https://tahir-abduro.netlify.app/\n\n" +
                               "Email: tahirabduro@gmail.com");
            alert.showAndWait();
        }
    }
    
    private void updateProgress() {
        if (playbackManager.getCurrentTrack() != null && !isSeeking) {
            // Update time labels
            currentTimeLabel.setText(playbackManager.getCurrentTimeFormatted());
            totalTimeLabel.setText(playbackManager.getTotalTimeFormatted());
            
            // Update progress slider
            double progress = playbackManager.getCurrentProgress();
            if (!Double.isNaN(progress)) {
                progressSlider.setValue(progress * 100);
            }
            
            // Update lyrics display
            updateLyricsDisplay();
            
            // Update playback state
            if (playbackManager.isPlaying() != isPlaying) {
                isPlaying = playbackManager.isPlaying();
                updateButtonSymbols();
            }
        }
    }
    
    private void updateButtonSymbols() {
        if (isPlaying) {
            playButton.setText("⏸");
            pauseButton.setVisible(false);
            pauseButton.setManaged(false);
            System.out.println("🔄 Button changed to: Pause (⏸)");
        } else {
            playButton.setText("▶");
            pauseButton.setVisible(true);
            pauseButton.setManaged(true);
            System.out.println("🔄 Button changed to: Play (▶)");
        }
        
        prevButton.setText("⏮");
        stopButton.setText("⏹");
        nextButton.setText("⏭");
    }
    
    // Album Art Methods
    private void setupAlbumArt() {
        System.out.println("=== Setting up Album Art ===");
        
        try {
            System.out.println("Trying to load: /com/app/media/default-album.png");
            URL resourceUrl = getClass().getResource("/com/app/media/default-album.png");
            if (resourceUrl != null) {
                System.out.println("✓ Found resource URL: " + resourceUrl);
                Image image = new Image(resourceUrl.toExternalForm());
                albumArtView.setImage(image);
                System.out.println("✓ Album art loaded successfully");
            } else {
                System.out.println("✗ Resource not found, creating default");
                createDefaultAlbumArt();
            }
        } catch (Exception e) {
            System.out.println("✗ Error loading album art: " + e.getMessage());
            createDefaultAlbumArt();
        }
        
        Rectangle clip = new Rectangle(250, 250);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        albumArtView.setClip(clip);
        System.out.println("✓ Rounded corners applied");
    }
    
    private void createDefaultAlbumArt() {
        System.out.println("Creating default album art...");
        
        Canvas canvas = new Canvas(250, 250);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
            new Stop(0, Color.DARKBLUE),
            new Stop(1, Color.PURPLE)
        );
        
        gc.setFill(gradient);
        gc.fillRect(0, 0, 250, 250);
        
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 80));
        gc.fillText("♪", 85, 160);
        
        WritableImage image = new WritableImage(250, 250);
        canvas.snapshot(null, image);
        albumArtView.setImage(image);
        
        System.out.println("✓ Default album art created");
    }
    
    private void updateAlbumArt(String trackName) {
        System.out.println("Updating album art for: " + trackName);
        
        int colorSeed = trackName.hashCode();
        Color[] colors = {
            Color.DARKBLUE, Color.DARKRED, Color.DARKGREEN, 
            Color.PURPLE, Color.TEAL, Color.MAROON
        };
        
        Color color = colors[Math.abs(colorSeed) % colors.length];
        
        Canvas canvas = new Canvas(250, 250);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
            new Stop(0, color),
            new Stop(1, color.brighter())
        );
        
        gc.setFill(gradient);
        gc.fillRect(0, 0, 250, 250);
        
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 80));
        gc.fillText("♪", 85, 160);
        
        if (!trackName.isEmpty()) {
            gc.setFont(javafx.scene.text.Font.font("Arial", 40));
            gc.fillText(String.valueOf(trackName.charAt(0)).toUpperCase(), 110, 100);
        }
        
        WritableImage image = new WritableImage(250, 250);
        canvas.snapshot(null, image);
        albumArtView.setImage(image);
        
        System.out.println("✓ Album art updated");
    }
    
    // Lyrics Methods
    private void setupLyricsDisplay() {
        System.out.println("=== Setting up Lyrics Display ===");
        currentLyricLabel.setText("🎵 Lyrics will appear here");
        lyricsStatusLabel.setText("Load audio file to auto-detect lyrics");
        
        // Clear upcoming lyrics box
        upcomingLyricsBox.getChildren().clear();
        Label instruction = new Label("Upcoming lyrics will appear here");
        instruction.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px; -fx-font-style: italic;");
        upcomingLyricsBox.getChildren().add(instruction);
        
        System.out.println("✓ Lyrics display initialized");
    }
    
    private void updateLyricsDisplay() {
        if (!currentLyrics.isEmpty() && playbackManager.getCurrentTime() != null) {
            Duration currentTime = playbackManager.getCurrentTime();
            String currentLyric = LyricsParser.getCurrentLyric(currentLyrics, currentTime);
            
            // Debug logging for lyrics
            System.out.println("🎵 Lyrics Debug:");
            System.out.println("  ⏱ Current time: " + formatTime(currentTime));
            System.out.println("  📝 Current lyric: " + currentLyric);
            System.out.println("  📊 Total lyrics loaded: " + currentLyrics.size());
            
            currentLyricLabel.setText(currentLyric);
            
            // Update upcoming lyrics
            updateUpcomingLyrics();
        } else {
            // Debug why lyrics aren't showing
            if (currentLyrics.isEmpty()) {
                System.out.println("🎵 No lyrics loaded in currentLyrics list");
            }
            if (playbackManager.getCurrentTime() == null) {
                System.out.println("🎵 No current time available from playbackManager");
            }
        }
    }
    
    private String formatTime(Duration duration) {
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
    
    private void updateUpcomingLyrics() {
        upcomingLyricsBox.getChildren().clear();
        
        if (!currentLyrics.isEmpty() && playbackManager.getCurrentTime() != null) {
            List<String> upcoming = LyricsParser.getUpcomingLyrics(
                currentLyrics, 
                playbackManager.getCurrentTime(), 
                3
            );
            
            if (upcoming.isEmpty()) {
                Label endLabel = new Label("🎵 End of lyrics");
                endLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px; -fx-font-style: italic;");
                upcomingLyricsBox.getChildren().add(endLabel);
            } else {
                for (String lyric : upcoming) {
                    Label lyricLabel = new Label(lyric);
                    lyricLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-padding: 2 0;");
                    lyricLabel.setWrapText(true);
                    lyricLabel.setMaxWidth(350);
                    upcomingLyricsBox.getChildren().add(lyricLabel);
                }
            }
        } else {
            Label noLyricsLabel = new Label("No lyrics loaded");
            noLyricsLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px; -fx-font-style: italic;");
            upcomingLyricsBox.getChildren().add(noLyricsLabel);
        }
    }
    
    private void loadLyricsForTrack(String audioFilePath) {
        currentLyrics.clear();
        currentAudioFile = audioFilePath;
        
        System.out.println("📄 Looking for lyrics for: " + audioFilePath);
        
        try {
            // Try to find lyrics file with same name as audio
            File audioFile = new File(audioFilePath);
            String baseName = audioFile.getName();
            baseName = baseName.substring(0, baseName.lastIndexOf('.'));
            
            File lyricsFile = null;
            String foundFormat = "";
            
            // Look for .lrc file first
            File lrcFile = new File(audioFile.getParent(), baseName + ".lrc");
            if (lrcFile.exists() && lrcFile.isFile()) {
                lyricsFile = lrcFile;
                foundFormat = "LRC";
                System.out.println("✓ Found LRC file: " + lrcFile.getAbsolutePath());
            } 
            // Then look for .txt file
            else {
                File txtFile = new File(audioFile.getParent(), baseName + ".txt");
                if (txtFile.exists() && txtFile.isFile()) {
                    lyricsFile = txtFile;
                    foundFormat = "TXT";
                    System.out.println("✓ Found TXT file: " + txtFile.getAbsolutePath());
                }
            }
            
            if (lyricsFile != null && lyricsFile.exists()) {
                currentLyrics = LyricsParser.parseLyricsFile(lyricsFile);
                System.out.println("✓ Successfully loaded " + currentLyrics.size() + " lyrics lines (" + foundFormat + " format)");
                lyricsStatusLabel.setText("Lyrics loaded: " + currentLyrics.size() + " lines (" + foundFormat + ")");
                
                // Update display with first lyric
                if (!currentLyrics.isEmpty()) {
                    currentLyricLabel.setText(currentLyrics.get(0).getText());
                    System.out.println("  First lyric: " + currentLyrics.get(0).getText());
                    System.out.println("  First timestamp: " + formatTime(currentLyrics.get(0).getTimestamp()));
                }
            } else {
                lyricsStatusLabel.setText("No lyrics file found (look for .lrc or .txt with same name)");
                currentLyricLabel.setText("No lyrics available for this track");
                System.out.println("ℹ️ No lyrics file found for: " + baseName);
                System.out.println("  Searched for: " + baseName + ".lrc and " + baseName + ".txt");
                System.out.println("  In folder: " + audioFile.getParent());
            }
            
        } catch (Exception e) {
            System.out.println("✗ Error loading lyrics: " + e.getMessage());
            e.printStackTrace();
            lyricsStatusLabel.setText("Error loading lyrics: " + e.getMessage());
            currentLyricLabel.setText("Lyrics error - check console");
        }
        
        updateUpcomingLyrics();
    }
}