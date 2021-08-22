package res;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static javafx.scene.input.KeyCode.H;
import static javafx.scene.input.KeyCode.X;

public class Main extends Application {

    private double xOffset = 0.0D;
    private double yOffset = 0.0D;

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        Scene scene = new Scene(root);
        root.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        scene.setOnKeyPressed(e ->{
            if(e.getCode() == H){
                if(stage.isIconified()){
                    stage.setAlwaysOnTop(true);
                }else{
                    stage.setIconified(true);
                }
            }else if (e.getCode() == X){
                stage.close();
            }
        });
        scene.setFill((Paint)null);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image(Main.class.getResource("images/music.png").toString()));
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
