package guifx;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class App extends Application {

    public void onResize() {

    }

    @Override
    public void start(Stage stage) {
        VBox vb = new VBox();

        HBox hb = new HBox();

        
        
        Scene s = new Scene(vb);
        s.setFill(Color.TRANSPARENT);

        stage.setScene(s);

        try {

            Board b = new Board(800, 800);
            hb.getChildren().add(b);
            
            ScrollPane sp = b.getSp();

            sp.setHbarPolicy(ScrollBarPolicy.NEVER);
            sp.setVbarPolicy(ScrollBarPolicy.NEVER);

            HBox.setHgrow(b, Priority.NEVER);
            HBox.setHgrow(sp, Priority.ALWAYS);

            b.setMaxWidth(b.getSquareSize() * 8);
            b.setMaxHeight(b.getSquareSize() * 8);
            b.setMinWidth(b.getSquareSize() * 8);
            b.setMinHeight(b.getSquareSize() * 8);

            hb.getChildren().add(sp);
            b.setViewOrder(0);
            sp.setViewOrder(1);

            s.setOnKeyReleased(b.keyHandler);
            // s.setOnKeyReleased(b.keyHandler);

            BarMenu menu = new BarMenu(b.getGame(), s.getWindow());
            b.getGame().addMoveListener(menu.getGameMenu());
            vb.getChildren().add(menu);
            vb.getChildren().add(hb);

        } catch (Exception e) {
            e.printStackTrace();
        }

        stage.show();
        stage.sizeToScene();

        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());


        // gr.fillRect(50, 50, 100, 100);

    }

    public static void main(String[] args) {

        launch();

    }

}