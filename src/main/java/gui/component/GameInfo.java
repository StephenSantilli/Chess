package gui.component;

import java.util.ArrayList;

import game.pieces.Piece;
import gui.GameView;
import gui.PieceTranscoder;
import gui.board.Board;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * A pane which displays information about the current game such as the timers,
 * captured pieces, and names of the players.
 */
public class GameInfo extends VBox {

    /**
     * The timer that is displayed at the top.
     */
    private GUITimer topTimer;

    /**
     * The timer that is displayed at the bottom.
     */
    private GUITimer bottomTimer;

    /**
     * The "history" timer that is displayed at the top. Used when the user is
     * looking at a past position to display the amount of time the player had on
     * the timer at the end of that move.
     */
    private GUITimerHistory topTimerHistory;

    /**
     * The "history" timer that is displayed at the bottom. Used when the user is
     * looking at a past position to display the amount of time the player had on
     * the timer at the end of that move.
     */
    private GUITimerHistory bottomTimerHistory;

    /**
     * The captured pieces view that is at the top.
     */
    private CapturedView topCap;

    /**
     * The captured pieces view that is at the bottom.
     */
    private CapturedView bottomCap;

    /**
     * The name of the player that is at the top.
     */
    private Label topName;

    /**
     * The name of the player that is at the bottom.
     */
    private Label bottomName;

    /**
     * The spacer that divides the information at the top and the bottom.
     */
    private Region spacer;

    /**
     * The GameView that contains this pane.
     */
    private GameView gameView;

    /**
     * The piece transcoders used to display the captured pieces.
     */
    private ArrayList<PieceTranscoder> transcoders;

    /**
     * Creates a new game info pane.
     * 
     * @param gameView The GameView that contains this info pane.
     */
    public GameInfo(GameView gameView) {

        this.gameView = gameView;
        transcoders = new ArrayList<>();
        try {
            initPieceTranscoders();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // - Top timer
        topName = new Label();
        topName.setId("nameLabel");
        topName.setAlignment(Pos.CENTER);

        topTimer = new GUITimer(gameView, !gameView.isFlipped());
        topTimer.setAlignment(Pos.CENTER);

        topTimerHistory = new GUITimerHistory(gameView, !gameView.isFlipped());
        topTimerHistory.setAlignment(Pos.CENTER);

        topCap = new CapturedView(gameView, !gameView.isFlipped());
        topCap.setAlignment(Pos.CENTER);

        VBox topInfoBox = new VBox(topName, topTimer, topTimerHistory, topCap);
        topInfoBox.setId("infoBox");
        topInfoBox.setAlignment(Pos.TOP_CENTER);

        // - Spacer
        spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // - Bottom timer
        bottomCap = new CapturedView(gameView, gameView.isFlipped());
        bottomCap.setAlignment(Pos.CENTER);

        bottomTimerHistory = new GUITimerHistory(gameView, gameView.isFlipped());
        bottomTimerHistory.setAlignment(Pos.CENTER);

        bottomTimer = new GUITimer(gameView, gameView.isFlipped());
        bottomTimer.setAlignment(Pos.CENTER);

        bottomName = new Label();
        bottomName.setId("nameLabel");

        bottomName.setAlignment(Pos.CENTER);

        VBox bottomInfoBox = new VBox(bottomCap, bottomTimerHistory, bottomTimer, bottomName);
        bottomInfoBox.setId("infoBox");
        topInfoBox.setAlignment(Pos.BOTTOM_CENTER);

        setId("infoPane");
        getChildren().addAll(topInfoBox, spacer, bottomInfoBox);

    }

    /**
     * Gets the top captured view.
     * 
     * @return {@link #topCap}
     */
    public CapturedView getTopCap() {
        return topCap;
    }

    /**
     * Gets the bottom captured view.
     * 
     * @return {@link #bottomCap}
     */
    public CapturedView getBottomCap() {
        return bottomCap;
    }

    /**
     * Gets the top timer view.
     * 
     * @return {@link #topTimer}
     */
    public GUITimer getTopTimer() {
        return topTimer;
    }

    /**
     * Sets the top timer view.
     * 
     * @param topTimer The timer to set it to.
     */
    public void setTopTimer(GUITimer topTimer) {
        this.topTimer = topTimer;
    }

    /**
     * Gets the bottom timer view.
     * 
     * @return {@link #bottomTimer}
     */
    public GUITimer getBottomTimer() {
        return bottomTimer;
    }

    /**
     * Sets the bottom timer view.
     * 
     * @param bottomTimer The timer to set it to.
     */
    public void setBottomTimer(GUITimer bottomTimer) {
        this.bottomTimer = bottomTimer;
    }

    /**
     * Gets the top timer history view.
     * 
     * @return {@link #topTimerHistory}
     */
    public GUITimerHistory getTopTimerHistory() {
        return topTimerHistory;
    }

    /**
     * Sets the top timer history view.
     * 
     * @param topHistory The timer history view to set it to.
     */
    public void setTopTimerHistory(GUITimerHistory topHistory) {
        this.topTimerHistory = topHistory;
    }

    /**
     * Gets the bottom timer history view.
     * 
     * @return {@link #bottomTimerHistory}
     */
    public GUITimerHistory getBottomTimerHistory() {
        return bottomTimerHistory;
    }

    /**
     * Sets the bottom timer history view.
     * 
     * @param bottomHistory The timer history view to set it to.
     */
    public void setBottomTimerHistory(GUITimerHistory bottomHistory) {
        this.bottomTimerHistory = bottomHistory;
    }

    /**
     * Gets the top name label.
     * 
     * @return {@link #topName}
     */
    public Label getTopName() {
        return topName;
    }

    /**
     * Sets the top name label.
     * 
     * @param topName The label to set the top name to.
     */
    public void setTopName(Label topName) {
        this.topName = topName;
    }

    /**
     * Gets the bottom name label.
     * 
     * @return {@link #bottomName}
     */
    public Label getBottomName() {
        return bottomName;
    }

    /**
     * Sets the bottom name label.
     * 
     * @param bottomName The label to set the bottom name to.
     */
    public void setBottomName(Label bottomName) {
        this.bottomName = bottomName;
    }

    /**
     * Updates the timers displayed in the info pane.
     */
    public void updateTimers() {

        topTimer.update();
        bottomTimer.update();
        topTimerHistory.update();
        bottomTimerHistory.update();
        topCap.draw();
        bottomCap.draw();

    }

    /**
     * Gets the corresponding {@link PieceTranscoder} for the type and color of the
     * piece given.
     * 
     * @param piece The piece to get the {@link PieceTranscoder} for
     * @return The {@link PieceTranscoder}
     */
    public PieceTranscoder getPieceTranscoder(Piece piece) {

        PieceTranscoder found = null;

        for (int i = 0; i < transcoders.size() && found == null; i++) {

            PieceTranscoder pt = transcoders.get(i);
            if (pt.isWhite() == piece.isWhite() && pt.getPieceCode() == piece.getCode())
                found = pt;

        }

        return found;

    }

    /**
     * Initializes the transcoders needed to display the captured pieces.
     * 
     * @throws Exception If there is an error transcoding the piece images.
     */
    public void initPieceTranscoders() throws Exception {

        final Board board = gameView.getBoard();
        if (board == null)
            return;

        final double pieceSize = board.getPieceSize();

        transcoders = new ArrayList<PieceTranscoder>();

        final double sizeMult = 1 / 3.0;
        boolean color = true;

        for (int i = 0; i < 2; i++) {

            transcoders.add(new PieceTranscoder(pieceSize * sizeMult, color, 'K'));
            transcoders.add(new PieceTranscoder(pieceSize * sizeMult, color, 'Q'));
            transcoders.add(new PieceTranscoder(pieceSize * sizeMult, color, 'R'));
            transcoders.add(new PieceTranscoder(pieceSize * sizeMult, color, 'B'));
            transcoders.add(new PieceTranscoder(pieceSize * sizeMult, color, 'N'));
            transcoders.add(new PieceTranscoder(pieceSize * sizeMult, color, 'P'));

            color = false;

        }

    }

}
