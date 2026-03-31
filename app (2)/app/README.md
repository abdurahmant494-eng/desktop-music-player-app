# Music Player Application

## Overview
This project is a JavaFX-based desktop music player application. It allows users to play audio tracks, manage playlists, and control playback through a user-friendly interface.

## Project Structure
The project is organized as follows:

```
app
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── module-info.java
│   │   │   └── com
│   │   │       └── app
│   │   │           ├── App.java
│   │   │           ├── Main.java
│   │   │           ├── Controller.java
│   │   │           ├── PlaybackManager.java
│   │   │           ├── Playlist.java
│   │   │           └── model
│   │   │               └── Track.java
│   │   └── resources
│   │       └── com
│   │           └── app
│   │               ├── MainScene.fxml
│   │               ├── primary.fxml
│   │               ├── style.css
│   │               └── media
│   │                   └── .gitkeep
│   └── test
│       └── java
│           └── com
│               └── app
│                   ├── PlaylistTest.java
│                   └── PlaybackManagerTest.java
├── pom.xml
├── README.md
└── .gitignore
```

## Features
- **Play Audio Tracks**: Load and play audio files from your local system.
- **Manage Playlists**: Create, modify, and save playlists of your favorite tracks.
- **Playback Controls**: Play, pause, stop, and navigate through tracks in the playlist.
- **User Interface**: Intuitive UI built with JavaFX, allowing for easy interaction.

## Setup Instructions
1. **Clone the Repository**: 
   ```bash
   git clone <repository-url>
   cd app
   ```

2. **Install Java 21 (LTS)**: Ensure Java 21 is installed and active on your PATH or point Maven to `JAVA_HOME`.

3. **Build the Project**: 
   Use Maven to build the project.
   ```bash
   mvn clean package
   ```

4. **Run the Application**: 
   Start the application using the following command:
   ```bash
   mvn javafx:run
   ```

## Dependencies
This project uses the following dependencies:
- Java 21 (LTS)
- JavaFX 21
- JUnit Jupiter for tests

## Testing
Unit tests are provided for the `Playlist` and `PlaybackManager` classes to ensure functionality. Run the tests using:
```bash
mvn test
```

## Contribution
Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.

## License
This project is licensed under the MIT License. See the LICENSE file for details.