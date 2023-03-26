package gui.component;

import java.util.Date;
import java.text.SimpleDateFormat;

import game.Chat;
import game.Game;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ChatBox extends VBox {

    private GameView gameView;

    private TextArea chat;
    private TextField enter;

    public ChatBox(GameView gameView) {

        this.gameView = gameView;

        chat = new TextArea();
        chat.setEditable(false);
        chat.setWrapText(true);

        enter = new TextField();
        enter.setOnAction(ev -> {

            final Game game = gameView.getGame();

            gameView.getGame().sendMessage(new Chat(game.getPlayer(gameView.getColor() == GameView.WHITE),
                    new Date().getTime(), enter.getText()));
            enter.setText("");

        });

        setSpacing(5);

        getChildren().addAll(chat, enter);

        update();

    }

    public void update() {

        Platform.runLater(() -> {

            if (gameView.getColor() == GameView.TWO_PLAYER) {
                chat.setDisable(true);
                enter.setDisable(true);
                return;
            } else {
                chat.setDisable(false);
                enter.setDisable(false);
            }

            chat.setText("");

            final Game game = gameView.getGame();

            for (Chat c : game.getMessages()) {

                if (!c.isSystemMessage())
                    chat.setText(chat.getText() + c.getPlayer().getName() + " ("
                            + SimpleDateFormat.getTimeInstance().format(new Date(c.getTimestamp())) + "): "
                            + c.getMessage()
                            + "\n");
                else {

                    chat.setText(chat.getText() + c.getMessage() + "\n");
                }

            }

            chat.positionCaret(chat.getLength());
            
        });

    }

}
