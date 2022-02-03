package bfst20.mapit;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.InputStream;

import javafx.application.Application;

public class Launcher extends Application {
    public void start(Stage primaryStage) throws Exception {
        Model model = new Model();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/View.fxml"));
        Scene scene = loader.load();
        Controller controller = loader.getController();

        controller.initialize(model, scene);

        primaryStage.setTitle("MapIt");
        InputStream stream = getClass().getClassLoader().getResourceAsStream("icons/UI/POI_UI.svg");
        primaryStage.getIcons().add(new Image(stream));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}