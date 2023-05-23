package gui.component;

import game.*;
import gui.GameView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * A display which shows the amount of time that is left on the clock for either
 * color.
 */
public class GUITimer extends Label {

    /**
     * Fromats the time to be in display format.
     * 
     * @param time The time, in milliseconds, to format.
     * @return The time in a formatted string.
     */
    public static String formatTime(long time) {

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
    /**
     * The GameView that contains this pane.
     */
    private GameView gameView;

    /**
     * Whether or not this timer displays white's time.
     */
    private boolean white;

    /**
     * The timeline of animations that display the countdown of the timer.
     */
    private final Timeline tl;

    /**
     * Creates a new timer display.
     * 
     * @param gameView The GameView that displays this pane.
     * @param white    Whether or not this timer displays white's time.
     */
    public GUITimer(GameView gameView, boolean white) {

        super(formatTime(gameView.getGame() == null ? 0
                : (gameView.getGame().getTimerTime(white))));

        this.gameView = gameView;
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

    /**
     * Gets whether or not this timer displays white's time.
     * 
     * @return {@link #white}
     */
    public boolean isWhite() {
        return white;
    }

    /**
     * Sets whether or not this timer displays white's time.
     * 
     * @param white If white.
     */
    public void setWhite(boolean white) {
        this.white = white;
    }

    /**
     * Updates the timer view based on the current amount of time left on the timer
     * and which color's turn it currently is.
     */
    public void update() {

        if (gameView.getGame() == null) {
            setText("");
            return;
        }
        if ((white == gameView.getGame().getLastPos().isWhite())
                && gameView.getGame().getSettings().getTimePerSide() > -1) {

            setId("guitimeractive");

            if (!gameView.getGame().isPaused() && gameView.getGame().getResult() == Game.Result.IN_PROGRESS)
                tl.play();
            else {
                tl.pause();
                frame();
            }

        } else {

            if ((white == gameView.getGame().getLastPos().isWhite()))
                setId("guitimeractive");
            else
                setId("guitimer");

            tl.pause();
            frame();

        }

    }

    /**
     * Animates a frame to display.
     */
    private void frame() {

        if (gameView.getGame() == null || gameView.getGame().getSettings().getTimePerSide() == -1) {
            setText("");
            return;
        }

        setText(formatTime(gameView.getGame().getTimerTime(white)));

    }

}
