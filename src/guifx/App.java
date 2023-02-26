package guifx;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

import org.girod.javafx.svgimage.*;

public class App extends Application {
    
    public void onResize() {



    }

    @Override
    public void start(Stage stage) {

       
        Group hb = new Group();

        Scene s = new Scene(hb, 800, 800);
        s.setFill(Color.TRANSPARENT);
        
        stage.setScene(s);

        stage.show();
        Board b = new Board(800,800);
        hb.getChildren().add(b);
        // gr.fillRect(50, 50, 100, 100);

    }

    public static void main(String[] args) {

        launch();

    }

}