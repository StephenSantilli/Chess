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

    private Board board;

    private boolean white;

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

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

    public GUITimer(Board board, boolean white) {

        super(formatTime(white ? board.getGame().getWhiteTime() : board.getGame().getBlackTime()));

        this.tl = new Timeline();
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.getKeyFrames().add(
                new KeyFrame(Duration.millis(50), new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent ev) {
                        frame();
                    }
                }));

        setFont(new Font(getFont().getName(), 24));

        this.board = board;
        this.white = white;

        if (board.getGame().isWhiteTurn(true) == white)
            setStyle("-fx-background-color:" + ACTIVE_BACKGROUND);
        else
            setStyle("-fx-background-color:" + INACTIVE_BACKGROUND);

    }

    public void frame() {
        setText(formatTime(white ? board.getGame().getWhiteTime() : board.getGame().getBlackTime()));
    }

    public void update() {

        if (board.getGame().isWhiteTurn(true) == white) {

            setStyle("-fx-background-color:" + ACTIVE_BACKGROUND);
            if (!board.getGame().isPaused() && board.getGame().getResult() == 0)
                tl.play();
            else {
                tl.pause();
                frame();
            }

        } else {

            setStyle("-fx-background-color:" + INACTIVE_BACKGROUND);
            tl.pause();
            frame();
            
        }

    }

}
