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

public class GameInfo extends VBox {

    private GUITimer topTimer, bottomTimer;
    private GUITimerHistory topTimerHistory, bottomTimerHistory;
    private CapturedView topCap, bottomCap;
    private Label topName, bottomName;
    private Region spacer;
    private GameView gameView;

    private ArrayList<PieceTranscoder> transcoders;

    public CapturedView getTopCap() {
        return topCap;
    }

    public CapturedView getBottomCap() {
        return bottomCap;
    }

    public GUITimer getTopTimer() {
        return topTimer;
    }

    public void setTopTimer(GUITimer topTimer) {
        this.topTimer = topTimer;
    }

    public GUITimer getBottomTimer() {
        return bottomTimer;
    }

    public void setBottomTimer(GUITimer bottomTimer) {
        this.bottomTimer = bottomTimer;
    }

    public GUITimerHistory getTopTimerHistory() {
        return topTimerHistory;
    }

    public void setTopTimerHistory(GUITimerHistory topHistory) {
        this.topTimerHistory = topHistory;
    }

    public GUITimerHistory getBottomTimerHistory() {
        return bottomTimerHistory;
    }

    public void setBottomTimerHistory(GUITimerHistory bottomHistory) {
        this.bottomTimerHistory = bottomHistory;
    }

    public Label getTopName() {
        return topName;
    }

    public void setTopName(Label topName) {
        this.topName = topName;
    }

    public Label getBottomName() {
        return bottomName;
    }

    public void setBottomName(Label bottomName) {
        this.bottomName = bottomName;
    }

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

    public void updateTimers() {

        topTimer.update();
        bottomTimer.update();
        topTimerHistory.update();
        bottomTimerHistory.update();
        topCap.draw();
        bottomCap.draw();

    }

    public Region getSpacer() {
        return spacer;
    }

    public void setSpacer(Region spacer) {
        this.spacer = spacer;
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
            if (pt.isColor() == piece.isWhite() && pt.getPieceCode() == piece.getCode())
                found = pt;

        }

        return found;

    }

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
