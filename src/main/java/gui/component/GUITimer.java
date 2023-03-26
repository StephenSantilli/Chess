package gui.component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class GUITimer extends Label {

    private GameView board;

    private boolean white;

    private final Timeline tl;

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public GUITimer(GameView board, boolean white) {

        super(formatTime(board.getGame() == null ? 0 : board.getGame().getTimerTime(white)));

        this.board = board;
        this.white = white;

        setId("guitimer");

        setMinWidth(100);

        this.tl = new Timeline();
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(50),
                ae -> {
                    frame();
                }));

        update();

    }

    public void update() {

        if (board.getGame() == null) {
            setText("");
            return;
        }

        if (board.getGame().getLastPos().isWhite() == white && board.getGame().getSettings().getTimePerSide() > -1) {

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

        long counted = time;

        long hours = counted / 1000 / 60 / 60;
        counted -= (hours * 1000 * 60 * 60);

        long minutes = counted / 1000 / 60;
        counted -= (minutes * 1000 * 60);

        long seconds = counted / 1000;
        counted -= (seconds * 1000);

        long tenths = counted / 100;
        counted -= (tenths * 100);

        str += (seconds < 10 && minutes > 0 ? "0" : "") + seconds;
        if (minutes > 0) {
            str = (minutes < 10 && hours > 0 ? "0" : "") + minutes + ":" + str;
        } else if (hours == 0) {
            str += "." + tenths;
        }

        if (hours > 0) {
            str = hours + ":" + str;
        }

        return str;

    }

    private void frame() {

        if (board.getGame() == null || board.getGame().getTimerTime(white) == -1) {
            setText("");
            return;
        }

        setText(formatTime(board.getGame().getTimerTime(white)));
    }

}
