package gui.component;

import game.*;
import gui.GameView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class GUITimerHistory extends Label {

    private GameView board;

    private boolean white;

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public GUITimerHistory(GameView board, boolean white) {

        super(formatTime(board.getGame() == null ? 0
                : (board.getGame().getTimerTime(white))));

        this.board = board;
        this.white = white;

        setId("guitimerhistory");

        setMinWidth(100);

        update();

    }

    public void update() {

        if (board.getGame() == null) {
            setText("");
            setVisible(false);
            return;
        }

        if (board.getGame().getPositions().size() - 1 == board.getCurrentPos()
                || board.getGame().getSettings().getTimePerSide() == -1) {
            setVisible(false);
            return;
        }

        if ((white == board.getGame().getPositions().get(board.getCurrentPos()).isWhite())
                && board.getGame().getSettings().getTimePerSide() > -1) {

            setId("guitimerhistoryactive");
            frame();

            setVisible(true);

        } else {

            setId("guitimerhistory");
            frame();

            setVisible(true);

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

        if (board.getGame() == null || board.getGame().getSettings().getTimePerSide() == -1) {
            setText("");
            return;
        }

        setText(formatTime(board.getGame().getTimerTime(white, board.getCurrentPos())));

    }

}
