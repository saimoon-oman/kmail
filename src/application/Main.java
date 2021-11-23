package application;

import java.io.FileInputStream;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
 
public class Main extends Application {
    
	public static String Domain = "@kuet.ac.bd";
	
	public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage mainStage) throws IOException {
    	Parent mainRoot = FXMLLoader.load(getClass().getResource("Main.fxml"));
    	
    	Scene mainScene = new Scene(mainRoot);
    	
		mainScene.getStylesheets().add(getClass().getResource("Main.css").toExternalForm());
    	
    	mainStage.setTitle("K-Mail");
    	
    	FileInputStream input = new FileInputStream("D:\\STUDY\\CODE\\JAVA\\HelloFX\\src\\application\\image.png");
        Image image = new Image(input);
        
    	mainStage.getIcons().add(image);
    	
        mainStage.setScene(mainScene);
    	mainStage.show();
    }
}