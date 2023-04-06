package gui;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.awt.Taskbar;
import java.awt.Toolkit;
import java.net.URL;
import java.util.prefs.Preferences;

import game.Game;
import gui.menu.BarMenu;

public class App extends Application {

    public static Preferences prefs = Preferences.userNodeForPackage(App.class);
    public static HostServices hostServices;

    private Stage stage;
    private Scene scene;

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public static void main(String[] args) {

        launch(args);
        System.exit(0);

    }

    @Override
    public void start(Stage stage) {

        App.hostServices = getHostServices();
        this.stage = stage;

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

        VBox view = new VBox();

        scene = new Scene(view);
        stage.setScene(scene);

        scene.getStylesheets().add(getClass().getResource("/css/style.css").toString());
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toString());

        try {

            BarMenu menu = new BarMenu();

            GameView gameView = new GameView(this, menu);
            VBox.setVgrow(gameView, Priority.ALWAYS);

            view.getChildren().addAll(menu, gameView);

            scene.setOnKeyReleased(gameView::keyHandler);

            stage.setOnShown(we -> gameView.startGame());
            stage.setOnCloseRequest(we -> {

                // TODO: save board position for resuming
                final Game game = gameView.getGame();
                if (game != null && game.getResult() == Game.Result.IN_PROGRESS)
                    game.markGameOver(Game.Result.TERMINATED, Game.Reason.OTHER);

                Platform.exit();

            });

            stage.show();
            stage.sizeToScene();

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

}