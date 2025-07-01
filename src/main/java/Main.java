import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Properties;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load config.properties from classpath
        Properties props = new Properties();
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                System.err.println("⚠️ config.properties not found on classpath!");
                return;
            }
            props.load(is);
        }

        // Load FXML and show stage
        Parent root = FXMLLoader.load(getClass().getResource("/View/MyView.fxml"));
        primaryStage.setTitle("Maze Game");
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
