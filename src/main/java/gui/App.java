package gui;

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
import java.util.prefs.Preferences;

import game.Game;

public class App extends Application {

    static Preferences prefs = Preferences.userNodeForPackage(App.class);

    @Override
    public void start(Stage stage) {

        // if (prefs.get("username", null) == null)
        //     prefs.put("username", "User");

        // if (prefs.get("timePerMove", null) == null)
        //     prefs.put("username", "User");

        // stage.setResizable(false);
        stage.setTitle("Chess " + Game.VERSION);
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

        VBox vb = new VBox();
        HBox hb = new HBox();

        Scene s = new Scene(vb);
        s.setFill(Color.TRANSPARENT);
        s.getStylesheets().add(getClass().getResource("/css/style.css").toString());

        stage.setScene(s);

        try {
            BarMenu menu = new BarMenu(s.getWindow());

            Board b = new Board(100, menu);
            hb.getChildren().add(b);

            vb.getChildren().addAll(menu, hb);

            ScrollPane sp = b.getScrollMovePane();

            sp.setHbarPolicy(ScrollBarPolicy.NEVER);
            sp.setVbarPolicy(ScrollBarPolicy.NEVER);

            HBox.setHgrow(b, Priority.NEVER);
            // HBox.setHgrow(sp, Priority.ALWAYS);

            // hb.setPadding(new Insets(5,5,5,5));

            hb.getChildren().add(sp);
            b.setViewOrder(0);
            sp.setViewOrder(1);

            s.setOnKeyReleased(b.getKeyHandler());

            stage.setOnCloseRequest(e -> {

                //TODO: save board position for resuming
                if (b.getGame() != null)
                    b.getGame().markGameOver(Game.RESULT_TERMINATED, Game.REASON_OTHER);

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