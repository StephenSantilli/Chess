package guifx;

import game.Game;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class GUITimer extends Label {

    private Game game;

    private boolean white;

    private static final String ACTIVE_BACKGROUND = "#dddddd";
    private static final String INACTIVE_BACKGROUND = "#eeeeee";

    private static String formatTime(long time) {

        String str = "";

        long minutes = time / 1000 / 60;
        long seconds = time / 1000 % 60;
        long tenths = (time - (minutes * 60 * 1000) - (seconds * 1000)) / 100;

        str += (seconds < 10 ? "0" : "") + seconds;
        if (minutes > 0) {
            str = minutes + ":" + str;
        } else {
            str += "." + tenths;
        }

        return str;

    }

    public GUITimer(Game game, boolean white) {

        super(formatTime(white ? game.getWhiteTime() : game.getBlackTime()));

        setFont(new Font(getFont().getName(), 24));

        this.game = game;
        this.white = white;

        if (game.isWhiteTurn(true) == white)
            setStyle("-fx-background-color:" + ACTIVE_BACKGROUND);
        else
            setStyle("-fx-background-color:" + INACTIVE_BACKGROUND);

    }

    public void update() {
 
        setText(formatTime(white ? game.getWhiteTime() : game.getBlackTime()));
        if (game.isWhiteTurn(true) == white)
            setStyle("-fx-background-color:" + ACTIVE_BACKGROUND);
        else
            setStyle("-fx-background-color:" + INACTIVE_BACKGROUND);

    }

}
