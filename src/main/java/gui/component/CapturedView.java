package gui.component;

import java.util.ArrayList;

import game.Position;
import game.pieces.Piece;
import gui.GameView;
import gui.PieceTranscoder;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CapturedView extends VBox {

    private GameView gv;

    /**
     * The color this captured view represents. Ex: If white is true, black pieces
     * will be displayed.
     */
    private boolean white;

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public CapturedView(GameView gv, boolean white) {

        this.gv = gv;
        this.white = white;
        setFillWidth(true);

    }

    public void draw() {

        getChildren().clear();

        if (gv.getGame() == null)
            return;

        final Position pos = gv.getGame().getPositions().get(gv.getCurrentPos());
        final ArrayList<Piece> captured = pos.getCapturedPieces(!white);

        ArrayList<Piece> colorCap = new ArrayList<>();

        captured.forEach(colorCap::add);

        HBox curr = new HBox();
        curr.setId("pieceBox");
        curr.setScaleX(-1);

        getChildren().add(curr);

        if (gv.isFlipped() == white)
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

            PieceTranscoder trans = gv.getInfoPane().getPieceTranscoder(a);
            ImageView im = trans.getImageView();
            im.setScaleX(-1);

            curr.getChildren().add(im);
            curr.setMinHeight(trans.getPieceSize());
            curr.setMaxWidth(Double.MAX_VALUE);

            im.setTranslateX((trans.getPieceSize() * (1 / 3.0) * (curr.getChildren().size() - 1)));

        }

    }

}
