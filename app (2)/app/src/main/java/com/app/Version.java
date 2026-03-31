package com.app;

/**
 * Version Information for Modern Music Player
 * 
 * @author Tahir Abduro
 * @version 1.0
 * @since 2024
 * @see <a href="https://tahir-abduro.netlify.app/">Portfolio</a>
 */
public class Version {
    public static final String APP_NAME = "Modern Music Player";
    public static final String VERSION = "1.0.0";
    public static final String DEVELOPER = "Tahir Abduro";
    public static final String PORTFOLIO = "https://tahir-abduro.netlify.app/";
    public static final String EMAIL = "tahirabduro@gmail.com";
    public static final String BUILD_DATE = "December 2024";
    public static final String LOCATION = "Addis Ababa, Ethiopia";
    
    public static String getInfo() {
        return String.format("""
            ========================================
            🎵 %s v%s
            ========================================
            Developed by: %s
            Portfolio: %s
            Email: %s
            Location: %s
            Build Date: %s
            ========================================
            """, 
            APP_NAME, VERSION, DEVELOPER, PORTFOLIO, EMAIL, LOCATION, BUILD_DATE);
    }
    
    public static String getAboutText() {
        return String.format("""
            %s v%s
            
            A professional music player with lyrics synchronization 
            and glass morphism UI.
            
            Developer Information:
            • Name: %s
            • Email: %s
            • Portfolio: %s
            • Location: %s
            
            Features:
            • Audio playback (MP3, WAV, AAC, FLAC, M4A, OGG)
            • Lyrics synchronization (LRC/TXT format)
            • Glass morphism UI design
            • Playlist management
            • Dynamic album art generation
            
            Technologies Used:
            • JavaFX 21
            • Maven
            • CSS3 for styling
            • Java Media API
            
            © 2024 %s. All rights reserved.
            """,
            APP_NAME, VERSION, DEVELOPER, EMAIL, PORTFOLIO, LOCATION, DEVELOPER);
    }
    
    public static void printWelcome() {
        System.out.println(getInfo());
    }
}