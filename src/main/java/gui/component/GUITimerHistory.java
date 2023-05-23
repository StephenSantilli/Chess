package gui.component;

import gui.GameView;
import javafx.scene.control.Label;

/**
 * A display which shows the timer history--the time on the timer of a past
 * move.
 */
public class GUITimerHistory extends Label {

    /**
     * The GameView that contains this pane.
     */
    private GameView gameView;

    /**
     * Whether or not this timer displays white's time.
     */
    private boolean white;

    /**
     * Creates a new timer history display.
     * 
     * @param gameView The GameView that displays this pane.
     * @param white    Whether or not this timer displays white's time.
     */
    public GUITimerHistory(GameView gameView, boolean white) {

        super(GUITimer.formatTime(gameView.getGame() == null ? 0
                : (gameView.getGame().getTimerTime(white))));

        this.gameView = gameView;
        this.white = white;

        setId("guitimerhistory");

        setMinWidth(100);

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
     * Updates the history view based on the current position being displayed in the
     * {@link #gameView}.
     */
    public void update() {

        if (gameView.getGame() == null) {
            setText("");
            setVisible(false);
            return;
        }

        if (gameView.getGame().getPositions().size() - 1 == gameView.getCurrentPos()
                || gameView.getGame().getSettings().getTimePerSide() == -1) {
            setVisible(false);
            return;
        }

        if ((white == gameView.getGame().getPositions().get(gameView.getCurrentPos()).isWhite())
                && gameView.getGame().getSettings().getTimePerSide() > -1) {

            setId("guitimerhistoryactive");
            frame();

            setVisible(true);

        } else {

            setId("guitimerhistory");
            frame();

            setVisible(true);

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

        setText(GUITimer.formatTime(gameView.getGame().getTimerTime(white, gameView.getCurrentPos())));

    }

}
