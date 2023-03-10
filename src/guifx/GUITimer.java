package guifx;

import game.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.util.Duration;

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

        str += (seconds < 10 && minutes > 0 ? "0" : "") + seconds;
        if (minutes > 0) {
            str = minutes + ":" + str;
        } else {
            str += "." + tenths;
        }

        return str;

    }

    private final Timeline tl;

    public GUITimer(Game game, boolean white) {

        super(formatTime(white ? game.getWhiteTime() : game.getBlackTime()));

        this.tl = new Timeline();
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.getKeyFrames().add(
            new KeyFrame(Duration.millis(50), new EventHandler<ActionEvent>() {
                public void handle(ActionEvent ev) {
                    frame();
                }
            })
        );
        

        setFont(new Font(getFont().getName(), 24));

        this.game = game;
        this.white = white;

        if (game.isWhiteTurn(true) == white)
            setStyle("-fx-background-color:" + ACTIVE_BACKGROUND);
        else
            setStyle("-fx-background-color:" + INACTIVE_BACKGROUND);

    }

    public void frame() {
        setText(formatTime(white ? game.getWhiteTime() : game.getBlackTime()));
    }

    public void update() {
 
        if (game.isWhiteTurn(true) == white) {
            setStyle("-fx-background-color:" + ACTIVE_BACKGROUND);
            tl.play();
        }
        else {
            setStyle("-fx-background-color:" + INACTIVE_BACKGROUND);
            tl.pause();
            frame();
        }

    }

}
