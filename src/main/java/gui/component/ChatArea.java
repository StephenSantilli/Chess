package gui.component;

import java.util.Date;
import game.Chat;
import game.Game;
import gui.GameView;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ChatArea extends VBox {

    private GameView gameView;

    private ChatHistoryBox historyBox;
    private ScrollPane scroller;

    private TextField enter;

    public ChatArea(GameView gameView) {

        this.gameView = gameView;

        historyBox = new ChatHistoryBox(gameView);
        scroller = new ScrollPane(historyBox);
        scroller.setFitToHeight(true);
        // scroller.setFitToWidth(true);
        setMaxWidth(Double.MAX_VALUE);

        enter = new TextField();
        enter.setPromptText("Enter chat message...");
        enter.setOnAction(ev -> {

            if (enter.getText().equals(""))
                return;

            final Game game = gameView.getGame();
            try {

                gameView.getGame().sendMessage(new Chat(game.getPlayer(gameView.getColor() == GameView.WHITE),
                        new Date().getTime(), enter.getText()));

                enter.setText("");

            } catch (Exception ex) {

                Dialog<Void> eDg = new Dialog<>();
                eDg.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
                eDg.setTitle("Error");
                eDg.setContentText("Error sending chat message: " + ex.getMessage());
                eDg.showAndWait();

            }

        });

        setSpacing(5);
        VBox.setVgrow(scroller, Priority.ALWAYS);
        VBox.setVgrow(enter, Priority.NEVER);
        setFillWidth(true);

        getChildren().addAll(scroller, enter);

        update();

    }

    public void update() {

        Platform.runLater(() -> {

            if (gameView.getColor() == GameView.TWO_PLAYER) {
                historyBox.setDisable(true);
                enter.setDisable(true);
                // historyBox.setVisible(false);
                // enter.setVisible(false);
                setVisible(false);
                return;
            } else {
                historyBox.setDisable(false);
                enter.setDisable(false);
                // historyBox.setVisible(true);
                // enter.setVisible(true);
                setVisible(true);
            }

            historyBox.draw(() -> {
                applyCss();
                scroller.layout();

                scroller.setVvalue(scroller.getVmax());
            });

        });

    }

}
