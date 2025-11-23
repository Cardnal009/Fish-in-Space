import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {

        @Override
        public void start(Stage stage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/SpaceInvader.fxml"));

            Scene scene = new Scene(root);
            stage.setMinWidth(454);
            stage.setMinHeight(540);
            stage.setTitle("Fish in Space");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/invaders/fishvader.png")));
            stage.setScene(scene);
            stage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }

}
