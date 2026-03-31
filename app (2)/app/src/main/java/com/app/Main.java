package com.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Print version information
        Version.printWelcome();
        
        // Load FXML
        Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
        
        // Set up stage
        primaryStage.setTitle("🎵 Modern Music Player - By Tahir Abduro");
        primaryStage.setScene(new Scene(root, 900, 700));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Show stage
        primaryStage.show();
        
        // Set up controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        loader.load();
        Controller controller = loader.getController();
        controller.setMainWindow(primaryStage);
        
        System.out.println("✓ Application started successfully");
        System.out.println("✓ Window title: Modern Music Player - By Tahir Abduro");
        System.out.println("✓ Developer portfolio: https://tahir-abduro.netlify.app/");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}