package gui;

import game.Move;
import game.Square;
import game.pieces.Piece;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class GUIPiece {

    private Piece piece;
    private ImageView image;
    private Board b;

    private Bounds boardBounds;
    private Bounds vBoxBounds;

    public Piece getPiece() {
        return piece;
    }

    public ImageView getImage() {
        return image;
    }

    public GUIPiece(Piece piece, ImageView image, Board board) {

        this.piece = piece;
        this.image = image;
        this.b = board;

        StackPane stack = board.getStack();

        this.boardBounds = stack.localToScene(stack.getBoundsInLocal());
        this.vBoxBounds = stack.localToScene(board.getBoundsInParent());

    }

    private void setPieceX(double x) {

        int relative = (int) vBoxBounds.getMinX();
        int offset = (int) boardBounds.getMinX();

        double ax = x - (b.getPieceSize() / 2.0) - relative;

        if (x >= boardBounds.getMinX() && x <= boardBounds.getMaxX())
            image.setLayoutX(ax);
        else if (x < boardBounds.getMinX())
            image.setLayoutX(boardBounds.getMinX() - (b.getPieceSize() / 2.0) - offset);
        else if (x > boardBounds.getMaxX())
            image.setLayoutX(boardBounds.getMaxX() - (b.getPieceSize() / 2.0) - offset);
    }

    private void setPieceY(double y) {

        int relative = (int) vBoxBounds.getMinY();
        int offset = (int) boardBounds.getMinY();

        double ay = y - (b.getPieceSize() / 2.0) - relative;

        if (y >= boardBounds.getMinY() && y <= boardBounds.getMaxY())
            image.setLayoutY(ay);
        else if (y < boardBounds.getMinY())
            image.setLayoutY(boardBounds.getMinY() - (b.getPieceSize() / 2.0) - offset);
        else if (y > boardBounds.getMaxY())
            image.setLayoutY(boardBounds.getMaxY() - (b.getPieceSize() / 2.0) - offset);

    }

    private void setPieceSquare(Square sq) {

        double x = b.getXBySquare(sq);
        double ax = x + ((b.getSquareSize() - b.getPieceSize()) / 2.0);

        double y = b.getYBySquare(sq);
        double ay = y + ((b.getSquareSize() - b.getPieceSize()) / 2.0);

        image.setLayoutX(ax);
        image.setLayoutY(ay);

    }

    /**
     * When the mouse has been pressed down.
     * 
     * @param ev The event of the mouse being pressed down.
     */
    public void onMousePressed(MouseEvent ev) {
        Square clickSquare = b.getSquareByLoc(ev.getSceneX(), ev.getSceneY(), true);

        if (b.getActive() != null
                && b.getGame().getLastPos().canPieceMoveToSquare(b.getActive().getPiece(),
                        clickSquare)) {

            int cPos = b.getGame().getPositions().size() - 1;

            try {

                Move m = new Move(b.getActive().getPiece().getSquare(),
                        clickSquare, b.getGame().getLastPos());

                b.setDragging(null);
                b.setActive(null);
                if (b.isTurn())
                    b.getGame().makeMove(m.getOrigin(), m.getDestination());

            } catch (Exception e) {
            }

            if (cPos == b.getCurrentPos()) {
                GUIPiece pc = b.getGUIPieceAtSquare(clickSquare);
                if (pc != null)
                    b.setActive(pc);
                else
                    b.setActive(null);

                b.setDragging(null);

                setPieceSquare(piece.getSquare());

            }

        } else if (b.getActive() == null
                || (!b.getGame().getPositions().get(b.getCurrentPos()).canPieceMoveToSquare(b.getActive().getPiece(),
                        clickSquare) && !b.getActive().getPiece().equals(this.getPiece()))
                ||
                clickSquare.equals(piece.getSquare())) {

            image.toFront();
            b.setActive(this);
            b.setDragging(this);

            b.updateActive();

            setPieceX(ev.getSceneX());
            setPieceY(ev.getSceneY());

            b.clearBorder();
            b.drawBorder(b.getXBySquare(getPiece().getSquare()), b.getYBySquare(getPiece().getSquare()));

        } else {

            b.setActive(null);
            b.setDragging(null);

            b.updateActive();
            b.clearBorder();

        }

    }

    /**
     * When the mouse has been dragged (with the mouse pressed down.)
     * 
     * @param ev The event of the mouse being dragged.
     */
    public void onMouseDragged(MouseEvent ev) {

        setPieceX(ev.getSceneX());
        setPieceY(ev.getSceneY());

        Square hoverSquare = b.getSquareByLoc(ev.getSceneX(), ev.getSceneY(), true);

        b.clearBorder();
        b.drawBorder(b.getXBySquare(hoverSquare), b.getYBySquare(hoverSquare));

    }

    /**
     * When the mouse has been lifted up.
     * 
     * @param ev The event of the mouse being lifted up.
     */
    public void onMouseReleased(MouseEvent ev) {

        b.clearBorder();

        if (b.getDragging() == null && (b.getActive() == null
                || (b.getActive() != null && b.getActive().getPiece().getSquare()
                        .equals(b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY(), true))))) {

            b.setDragging(null);

            b.updateActive();

            return;

        } else if (b.getActive() != null && b.getDragging() != null && b.getActive().getPiece().getSquare()
                .equals(b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY(), true))) {

            b.setDragging(null);

            setPieceSquare(piece.getSquare());

            return;

        }

        if (b.getDragging() != null) {

            int cPos = b.getCurrentPos();

            try {

                Piece d = b.getDragging().getPiece();

                b.setActive(null);

                Move m = new Move(d.getSquare(),
                        b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY(), true),
                        b.getGame().getPositions().get(b.getCurrentPos()));
                if (b.isTurn())
                    b.getGame().makeMove(m.getOrigin(), m.getDestination());

            } catch (Exception e) {

            }

            if (cPos == b.getCurrentPos()) {

                GUIPiece pc = b.getGUIPieceAtSquare(
                        b.getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY(), true));

                if (pc != null)
                    b.setActive(pc);
                else
                    b.setActive(null);

                b.setDragging(null);
                setPieceSquare(piece.getSquare());

            }

        } else
            setPieceSquare(piece.getSquare());

    }

}