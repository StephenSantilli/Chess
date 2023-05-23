package gui.component;

import java.util.ArrayList;

import game.Position;
import game.pieces.Piece;
import gui.GameView;
import gui.PieceTranscoder;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * A pane which displays all of the pieces that a certain color has captured, as
 * well as the positive point delta they may have.
 */
public class CapturedView extends VBox {

    /**
     * The GameView which contains this view.
     */
    private GameView gameView;

    /**
     * The color this captured view represents. Ex: If white is true, black pieces
     * will be displayed.
     */
    private boolean white;

    /**
     * Creates a new captured view.
     * @param gameView The GameView which will contain this view.
     * @param white Whether or not this view should represent white's captures. (If {@code true}, black pieces will be displayed.)
     */
    public CapturedView(GameView gameView, boolean white) {

        this.gameView = gameView;
        this.white = white;

    }

    /**
     * Gets the color this captured view represents.
     * 
     * @return {@link #white}
     * @see #white
     */
    public boolean isWhite() {
        return white;
    }

    /**
     * Sets {@link #white}
     * 
     * @param white Whether or not this captured view should represent white.
     */
    public void setWhite(boolean white) {
        this.white = white;
    }

    /**
     * Redraws the view.
     */
    public void draw() {

        getChildren().clear();

        if (gameView.getGame() == null)
            return;

        final Position pos = gameView.getGame().getPositions().get(gameView.getCurrentPos());
        final ArrayList<Piece> captured = pos.getCapturedPieces(!white);

        ArrayList<Piece> colorCap = new ArrayList<>();

        captured.forEach(colorCap::add);

        HBox curr = new HBox();
        curr.setId("pieceBox");
        curr.setScaleX(-1);

        getChildren().add(curr);

        if (gameView.isFlipped() == white)
            colorCap.sort((p1, p2) -> p2.getPoints() - p1.getPoints());
        else
            colorCap.sort((p1, p2) -> p1.getPoints() - p2.getPoints());

        for (int i = colorCap.size() - 1; i >= 0; i--) {

            final Piece a = colorCap.get(i);

            if (i < colorCap.size() - 1 && colorCap.get(i + 1).getCode() != a.getCode()) {

                curr = new HBox();
                curr.setId("pieceBox");
                curr.setScaleX(-1);
                getChildren().add(curr);

            }

            PieceTranscoder trans = gameView.getGameInfoPane().getPieceTranscoder(a);
            ImageView im = trans.toImageView();
            im.setScaleX(-1);

            curr.getChildren().add(im);
            curr.setMinHeight(trans.getPieceSize());
            curr.setMaxWidth(Double.MAX_VALUE);

            im.setTranslateX((trans.getPieceSize() * (1 / 3.0) * (curr.getChildren().size() - 1)));

        }

        int delta = gameView.getGame().getPositions().get(gameView.getCurrentPos()).calculatePieceDelta();
        if ((white && delta > 0) || (!white && delta < 0)) {
            Label l = new Label("+" + (int) Math.abs(delta) + "");
            l.setAlignment(Pos.CENTER_RIGHT);
            Region r = new Region();
            HBox.setHgrow(r, Priority.ALWAYS);
            HBox deltaBox = new HBox(r, l);

            if (gameView.isFlipped() != white)
                getChildren().add(0, deltaBox);
            else
                getChildren().add(deltaBox);

        }

    }

}
