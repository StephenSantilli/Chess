package guifx;

import game.Game;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.awt.Taskbar;
import java.awt.Toolkit;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        VBox vb = new VBox();
        HBox hb = new HBox();

        Scene s = new Scene(vb);
        s.setFill(Color.TRANSPARENT);

        stage.setTitle("Chess");
        stage.getIcons().add(new Image(getClass().getResource("/img/icon_16x16.png").toString()));
        stage.getIcons().add(new Image(getClass().getResource("/img/icon_24x24.png").toString()));
        stage.getIcons().add(new Image(getClass().getResource("/img/icon_32x32.png").toString()));
        stage.getIcons().add(new Image(getClass().getResource("/img/icon_48x48.png").toString()));
        stage.getIcons().add(new Image(getClass().getResource("/img/icon_256x256.png").toString()));
        stage.getIcons().add(new Image(getClass().getResource("/img/icon_512x512.png").toString()));

        // Sets taskbar icon on Mac
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            try {
                taskbar.setIconImage(Toolkit.getDefaultToolkit()
                        .getImage(getClass().getResource("/img/icon_512x512.png")));
            } catch (Exception e) {
            }
        }

        stage.setScene(s);

        try {
            
            BarMenu menu = new BarMenu(s.getWindow());
            vb.getChildren().addAll(menu, hb);

            Board b = new Board(100, menu);
            hb.getChildren().add(b);

            ScrollPane sp = b.getScrollMovePane();

            sp.setHbarPolicy(ScrollBarPolicy.NEVER);
            sp.setVbarPolicy(ScrollBarPolicy.NEVER);

            HBox.setHgrow(b, Priority.NEVER);
            HBox.setHgrow(sp, Priority.ALWAYS);

            hb.getChildren().add(sp);
            b.setViewOrder(0);
            sp.setViewOrder(1);

            s.setOnKeyReleased(b.keyHandler);


            stage.setOnCloseRequest(e -> {

                if (b.getGame().getResult() <= Game.RESULT_IN_PROGRESS) {
                    b.getGame().markGameOver(Game.RESULT_TERMINATED, Game.REASON_OTHER);
                }

                Platform.exit();

            });

            stage.setOnShown(b::startGame);

            stage.show();
            stage.sizeToScene();

            stage.setMinHeight(stage.getHeight());
            stage.setMinWidth(stage.getWidth());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        launch(args);
        System.exit(0);

    }

}