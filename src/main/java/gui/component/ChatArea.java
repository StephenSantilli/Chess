package gui.component;

import java.util.Date;
import java.text.SimpleDateFormat;

import game.Chat;
import game.Game;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
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
        scroller.setFitToWidth(true);

        enter = new TextField();
        enter.setPromptText("Enter chat message...");
        enter.setOnAction(ev -> {

            final Game game = gameView.getGame();

            gameView.getGame().sendMessage(new Chat(game.getPlayer(gameView.getColor() == GameView.WHITE),
                    new Date().getTime(), enter.getText()));
            enter.setText("");

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
                return;
            } else {
                historyBox.setDisable(false);
                enter.setDisable(false);
            }

            historyBox.draw();

        });

    }

}
