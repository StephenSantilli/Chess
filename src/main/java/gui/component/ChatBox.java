package gui.component;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import game.Chat;
import game.Game;
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

        enter = new TextField();
        enter.setOnAction(ev -> {

            final Game game = gameView.getGame();

            gameView.getGame().sendMessage(new Chat(game.getPlayer(gameView.getColor() == GameView.WHITE),
                    new Date().getTime(), enter.getText()));
            enter.setText("");

        });

        getChildren().addAll(chat, enter);

        update();

    }

    public void update() {

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

            chat.setText(chat.getText() + c.getPlayer().getName() + " ("
                    + SimpleDateFormat.getTimeInstance().format(new Date(c.getTimestamp())) + "): " + c.getMessage()
                    + "\n");

        }

    }

}
