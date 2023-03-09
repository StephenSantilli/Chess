package guifx;

import game.Move;
import game.Piece;
import game.Square;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class GUIPiece {

    private Piece piece;
    private ImageView image;
    private Board b;

    private Bounds bds;

    public GUIPiece(Piece piece, ImageView image, Board board) {

        this.piece = piece;
        this.image = image;
        this.b = board;

        this.bds = b.localToParent(b.getBoundsInLocal());

    }

    public Piece getPiece() {
        return piece;
    }

    public ImageView getImage() {
        return image;
    }

    private void setPieceX(double x) {

        int relative = (int) bds.getMinX();

        double ax = x - (b.getPieceSize() / 2.0) - relative;

        if (x >= bds.getMinX() && x <= bds.getMaxX())
            image.setLayoutX(ax);
        else if (x < bds.getMinX()) {
            image.setLayoutX(bds.getMinX() - (b.getPieceSize() / 2.0) - relative);
        } else if (x > bds.getMaxX())
            image.setLayoutX(bds.getMaxX() - (b.getPieceSize() / 2.0) - relative);
    }

    private void setPieceY(double y) {

        int relative = (int) bds.getMinY();

        double ay = y - (b.getPieceSize() / 2.0) - relative;

        if (y >= bds.getMinY() && ay <= bds.getMaxY())
            image.setLayoutY(ay);
        else if (y < bds.getMinY()) {
            image.setLayoutY(bds.getMinY() - (b.getPieceSize() / 2.0) - relative);
        } else if (y > bds.getMaxY())
            image.setLayoutY(bds.getMaxY() - (b.getPieceSize() / 2.0) - relative);
    }

    public void onMouseDragged(MouseEvent ev) {

        setPieceX(ev.getSceneX());

        setPieceY(ev.getSceneY());

        b.clearBorder();
        Square hoverSquare = b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY());
        b.drawBorder(b.getXBySquare(hoverSquare), b.getYBySquare(hoverSquare));

    }

    public void onMousePressed(MouseEvent ev) {

        GUIPiece gp = b.getGUIPieceAtSquare(b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()));

        if (b.getActive() != null && gp != null
                && b.getGame().getActivePos().canPieceMoveToSquare(b.getActive().getPiece(),
                        gp.getPiece().getSquare())) {
            int cPos = b.getGame().getCurrentPos();
            try {
                Move m = new Move(b.getActive().getPiece().getSquare(),
                        b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()), b.getGame().getActivePos());

                b.setDragging(null);
                b.setActive(null);
                b.getGame().makeMove(m);
                b.boardUpdated(true, b.getGame().getActivePos(),
                        b.getGame().getPositions().get(b.getGame().getPositions().size() - 1));

            } catch (Exception e) {

            }

            if (cPos == b.getGame().getCurrentPos()) {
                GUIPiece pc = b.getGUIPieceAtSquare(
                        b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()));
                if (pc != null) {
                    b.setActive(pc);
                } else {
                    b.setActive(null);
                }
                b.setDragging(null);
                b.boardUpdated(false, null, null);

            }

        } else {

            image.toFront();
            b.setActive(gp);
            b.setDragging(gp);

            b.updateActive();

            image.setLayoutX(ev.getSceneX() - (b.getPieceSize() / 2.0));
            image.setLayoutY(ev.getSceneY() - (b.getPieceSize() / 2.0));

            b.clearBorder();
            b.drawBorder(b.getXBySquare(gp.getPiece().getSquare()), b.getYBySquare(gp.getPiece().getSquare()));

        }

    }

    public void onMouseReleased(MouseEvent ev) {

        b.clearBorder();
        if (b.getDragging() == null && (b.getActive() == null
                || (b.getActive() != null && b.getActive().getPiece().getSquare()
                        .equals(b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()))))) {

            b.setDragging(null);

            b.boardUpdated(false, null, null);
            // updateActive();
            return;

        } else if (b.getActive() != null && b.getDragging() != null && b.getActive().getPiece().getSquare()
                .equals(b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()))) {

            b.setDragging(null);
            b.boardUpdated(false, null, null);
            return;

        }

        if (b.getDragging() != null) {

            int cPos = b.getGame().getCurrentPos();
            try {

                Piece d = b.getDragging().getPiece();
                b.setActive(null);
                b.setDragging(null);
                b.getGame().makeMove(new Move(d.getSquare(),
                        b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()), b.getGame().getActivePos()));
                b.boardUpdated(false, b.getGame().getActivePos(), null);

            } catch (Exception e) {

            }

            if (cPos == b.getGame().getCurrentPos()) {
                GUIPiece pc = b.getGUIPieceAtSquare(
                        b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()));

                if (pc != null) {
                    b.setActive(pc);
                } else {
                    b.setActive(null);
                }
                b.setDragging(null);
                b.boardUpdated(false, null, null);

            }

        } else if (b.getActive() != null) {

            int cPos = b.getGame().getCurrentPos();
            try {
                Move m = new Move(b.getActive().getPiece().getSquare(),
                        b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()), b.getGame().getActivePos());

                b.setDragging(null);
                b.setActive(null);
                b.getGame().makeMove(m);
                b.boardUpdated(true, b.getGame().getActivePos(),
                        b.getGame().getPositions().get(b.getGame().getPositions().size() - 1));

            } catch (Exception e) {

            }

            if (cPos == b.getGame().getCurrentPos()) {
                GUIPiece pc = b.getGUIPieceAtSquare(
                        b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()));
                if (pc != null) {
                    b.setActive(pc);
                } else {
                    b.setActive(null);
                }
                b.setDragging(null);
                b.boardUpdated(false, null, null);

            }

        } else {
            b.boardUpdated(false, null, null);
        }

    }

}
