package gui;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.awt.Taskbar;
import java.awt.Toolkit;

import java.util.Optional;
import java.util.prefs.Preferences;

import game.Game;
import gui.dialog.Export;
import gui.menu.BarMenu;

/**
 * The main application class that displays the GUI.
 */
public class App extends Application {

    /**
     * The preferences the user has set.
     */
    public static Preferences prefs = Preferences.userNodeForPackage(App.class);

    /**
     * The host services of the app. Used to open web pages in the user's browser.
     */
    public static HostServices hostServices;

    public static void main(String[] args) {

        launch(args);
        System.exit(0);

    }

    /**
     * The main stage of the GUI.
     */
    private Stage stage;

    /**
     * The main scene of the GUI.
     */
    private Scene scene;

    /**
     * Gets the main stage of the GUI.
     * 
     * @return {@link #stage}
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Gets the main scene of the GUI.
     * 
     * @return {@link #scene}
     */
    public Scene getScene() {
        return scene;
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

                we.consume();

                final Game game = gameView.getGame();

                Dialog<ButtonType> confirmCloseDialog = new Dialog<>();
                confirmCloseDialog.setTitle("Are you sure?");
                confirmCloseDialog.setContentText("Are you sure? If you quit you will lose your game.");
                confirmCloseDialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO,
                        new ButtonType("Export", ButtonData.OTHER));

                if (gameView.getGame() != null) {
                    Optional<ButtonType> res = confirmCloseDialog.showAndWait();

                    if (res.get().getButtonData().equals(ButtonData.OTHER)) {

                        Export save = new Export(gameView);

                        save.showAndWait();

                    } else if (res.get().equals(ButtonType.YES)) {

                        if (game != null && game.getResult() == Game.Result.IN_PROGRESS)
                            game.markGameOver(Game.Result.TERMINATED, Game.Reason.OTHER);

                        Platform.exit();

                    } else {
                        confirmCloseDialog.hide();
                    }
                } else {
                    Platform.exit();
                }

            });

            stage.show();
            stage.sizeToScene();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}