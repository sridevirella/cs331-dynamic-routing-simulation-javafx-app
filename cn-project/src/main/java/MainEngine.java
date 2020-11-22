import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import views.GuiElements;

import java.io.IOException;

public class MainEngine extends Application {

    private static GuiElements views;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        views = new GuiElements(primaryStage);
        createScene(primaryStage);
    }

    private void createScene(Stage stage) {

        Scene scene = new Scene(views.getBorderPane(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Dynamic Routing Simulation");
        stage.show();
    }
}
