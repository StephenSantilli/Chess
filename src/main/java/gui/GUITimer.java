package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GUITimer extends Label {

    private Board board;

    private boolean white;

    private final Timeline tl;

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public GUITimer(Board board, boolean white) {

        super(formatTime(board.getGame() == null ? 0 : board.getGame().getTimerTime(white)));

        setId("guitimer");

        setMinWidth(100);

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

        

        if (board.getGame() != null && board.getGame().getLastPos().isWhite() == white)
            setId("guitimeractive");
        else
            setId("guitimer");

    }

    public void update() {

        if (board.getGame().getLastPos().isWhite() == white) {

            setId("guitimeractive");

            if (!board.getGame().isPaused() && board.getGame().getResult() == 0)
                tl.play();
            else {
                tl.pause();
                frame();
            }

        } else {

            setId("guitimer");

            tl.pause();
            frame();

        }

    }

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

    private void frame() {
        setText(formatTime(board.getGame().getTimerTime(white)));
    }

}
