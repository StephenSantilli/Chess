package gui.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import game.Chat;
import game.Game;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ChatHistoryBox extends VBox {

    private GameView gameView;

    public ChatHistoryBox(GameView gameView) {

        this.gameView = gameView;

        draw();

    }

    public void draw() {

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

                for (int j = 0; j < game.getMessages().size(); j++) {

                    Chat cj = game.getMessages().get(i + j);

                    if (!cj.getPlayer().equals(c.getPlayer()) || cj.isSystemMessage()) {
                        i += j - 1;
                        break;
                    }

                    Label name = new Label(cj.getPlayer().getName());
                    Label timestamp = new Label(dFormat.format(new Date(cj.getTimestamp())));
                    Label message = new Label(cj.getMessage());

                    content.add(name, 0, j);
                    content.add(timestamp, 0, j);
                    content.add(message, 0, j);

                }

            } else {

                Label message = new Label(c.getMessage());
                if (c.isError())
                    message.setTextFill(Color.RED);

                content.add(message, 0, i);

            }

            getChildren().add(content);

        }

    }

}
