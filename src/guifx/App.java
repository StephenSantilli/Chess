package guifx;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.ScrollPane;
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

public class App extends Application {

    public void onResize() {

    }

    @Override
    public void start(Stage stage) {
        VBox vb = new VBox();

        HBox hb = new HBox();


        vb.getChildren().add(hb);
        
        
        Scene s = new Scene(vb, 1020, 800);
        s.setFill(Color.TRANSPARENT);

        stage.setScene(s);

        stage.show();
        try {

            Board b = new Board(800, 800);
            hb.getChildren().add(b);
            ScrollPane sp = new ScrollPane(b.getMp());
            b.getGame().addMoveListener(b.getMp());
            b.getMp().initMovePane();
            sp.setFitToWidth(true);
            sp.setMinWidth(220);
            hb.getChildren().add(sp);

            HBox.setHgrow(b, Priority.NEVER);
            HBox.setHgrow(sp, Priority.ALWAYS);
            
            b.setMaxWidth(b.getSquareSize() * 8);
            b.setMaxHeight(b.getSquareSize() * 8);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // gr.fillRect(50, 50, 100, 100);

    }

    public static void main(String[] args) {

        launch();

    }

}