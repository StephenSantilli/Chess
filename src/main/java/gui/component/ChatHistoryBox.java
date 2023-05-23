package gui.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import game.Chat;
import game.Game;
import gui.GameView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * The view which displays the previous chats sent.
 */
public class ChatHistoryBox extends VBox {

    /**
     * The GameView which contains this box.
     */
    private GameView gameView;

    /**
     * Creates a new chat history box.
     * 
     * @param gameView The GameView which contains this chat history box.
     */
    public ChatHistoryBox(GameView gameView) {

        this.gameView = gameView;

        draw(null);

    }

    /**
     * Draws the chat history.
     * 
     * @param callback A callback to be executed when drawing the chats is complete.
     */
    public void draw(Runnable callback) {

        getChildren().clear();

        final Game game = gameView.getGame();

        if (game == null)
            return;

        final SimpleDateFormat dFormat = new SimpleDateFormat("h:mm a");

        for (int i = 0; i < game.getMessages().size(); i++) {

            Chat c = game.getMessages().get(i);
            GridPane content = new GridPane();
            content.setHgap(2);

            if (!c.isSystemMessage()) {

                Text name = new Text(
                        (c.getPlayer().getName().length() > 12 ? c.getPlayer().getName().substring(0, 9) + "..."
                                : c.getPlayer().getName()) + ":");
                name.setId("chatName");

                Text timestamp = new Text(dFormat.format(new Date(c.getTimestamp())));
                timestamp.setId("chatTimestamp");

                Text message = new Text(c.getMessage());
                message.setId("chatMessage");

                content.add(name, 0, 0);
                // content.add(timestamp, 1, 0);
                content.add(message, 2, 0);

                for (int j = 1; i + j <= game.getMessages().size(); j++) {

                    if (i + j == game.getMessages().size()) {
                        i += j - 1;
                        break;
                    }

                    Chat cj = game.getMessages().get(i + j);

                    if (!cj.getPlayer().equals(c.getPlayer()) || cj.isSystemMessage()) {
                        i += j - 1;
                        break;
                    }

                    Text cmessage = new Text(cj.getMessage());
                    cmessage.setId("chatName");

                    content.add(cmessage, 2, j);

                }

            } else {

                Text message = new Text(c.getMessage());

                if (c.isError())
                    message.setFill(Color.RED);

                content.add(message, 0, i);

            }

            getChildren().add(content);

        }

        if (callback != null)
            callback.run();

    }

}
