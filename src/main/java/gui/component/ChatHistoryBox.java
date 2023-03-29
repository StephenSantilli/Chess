package gui.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import game.Chat;
import game.Game;
import gui.GameView;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ChatHistoryBox extends VBox {

    private GameView gameView;

    public ChatHistoryBox(GameView gameView) {

        this.gameView = gameView;

        draw(null);

    }

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

                Label name = new Label(c.getPlayer().getName());
                Label timestamp = new Label(dFormat.format(new Date(c.getTimestamp())));
                Label message = new Label(c.getMessage());

                content.add(name, 0, 0);
                content.add(timestamp, 1, 0);
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

                    Label cmessage = new Label(cj.getMessage());

                    content.add(cmessage, 2, j);

                }

            } else {

                Label message = new Label(c.getMessage());
                if (c.isError())
                    message.setTextFill(Color.RED);

                content.add(message, 0, i);

            }
            
            getChildren().add(content);

        }

        if (callback != null)
            callback.run();

    }

}
