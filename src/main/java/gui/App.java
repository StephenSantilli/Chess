package gui;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.awt.Taskbar;
import java.awt.Toolkit;
import java.net.URL;
import java.util.prefs.Preferences;

import game.Game;
import gui.component.GameView;
import gui.menu.BarMenu;

public class App extends Application {

    public static Preferences prefs = Preferences.userNodeForPackage(App.class);

    private Stage stage;
    private Scene scene;

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    @Override
    public void start(Stage stage) {

        this.stage = stage;

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

        scene = new Scene(vb);

        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toString());
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toString());

        stage.setScene(scene);

        try {

            BarMenu menu = new BarMenu(scene.getWindow());

            GameView b = new GameView(this, menu);

            VBox.setVgrow(b, Priority.ALWAYS);

            vb.getChildren().addAll(menu, b);

            scene.setOnKeyReleased(b::keyHandler);

            stage.setOnShown(b::startGame);

            stage.setOnCloseRequest(e -> {

                // TODO: save board position for resuming
                if (b.getGame() != null && b.getGame().getResult() == Game.RESULT_IN_PROGRESS)
                    b.getGame().markGameOver(Game.RESULT_TERMINATED, Game.REASON_OTHER);

                Platform.exit();

            });

            stage.show();
            stage.sizeToScene();

            stage.setMinHeight(stage.getHeight());
            stage.setMinWidth(stage.getWidth());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setTheme(String name) throws Exception {

        URL sheet = null;
        try {
            sheet = getClass().getResource("/css/" + name + ".css");
        } catch (Exception e) {
            throw new Exception("Theme not found.");
        }

        scene.getStylesheets().add(sheet.toString());
    }

    public static void main(String[] args) {

        launch(args);
        System.exit(0);

    }

}