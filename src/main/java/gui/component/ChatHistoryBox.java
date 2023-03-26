package gui.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import game.Chat;
import game.Game;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class ChatHistoryBox extends GridPane {

    private GameView gameView;

    public ChatHistoryBox(GameView gameView) {

        this.gameView = gameView;

        draw();

    }

    public void draw() {

        final Game game = gameView.getGame();

        if (game == null)
            return;

        final SimpleDateFormat dFormat = new SimpleDateFormat("h:mm a");

        int i = 0;

        for (Chat c : game.getMessages()) {

            if (!c.isSystemMessage()) {

                Label name = new Label(c.getPlayer().getName());
                Label timestamp = new Label(dFormat.format(new Date(c.getTimestamp())));
                Label message = new Label(c.getMessage());

                add(name, 0, i);
                add(timestamp, 1, i);
                add(message, 2, i);

            } else {

                Label message = new Label(c.getMessage());
                if (c.isError())
                    message.setTextFill(Color.RED);

                add(message, 0, i);

            }

            i++;

        }

    }

}
