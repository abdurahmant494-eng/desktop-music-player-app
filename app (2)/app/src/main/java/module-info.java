module com.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;  // Add this line
    
    opens com.app to javafx.fxml;
    exports com.app;
}