package gui.board;

import java.util.ArrayList;

import game.Move;
import game.Square;
import game.pieces.Piece;
import gui.GameView;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class GUIPiece {

    private Piece piece;
    private ImageView image;
    private GameView b;

    private char promoteResponse;
    private Move promoteMove;

    private Runnable promoteCallback = () -> {

        if (promoteMove == null || promoteResponse == '0')
            return;

        if (promoteResponse == 'X') {

            b.getBoard().boardUpdated();
            promoteResponse = '0';
            promoteMove = null;

        }

        else {

            try {
                b.getGame().makeMove(promoteMove.getOrigin(), promoteMove.getDestination(), promoteResponse);
            } catch (Exception e) {

            }

        }

    };

    public Runnable getPromoteCallback() {
        return promoteCallback;
    }

    public char getPromoteResponse() {
        return promoteResponse;
    }

    public void setPromoteResponse(char promoteResponse) {
        this.promoteResponse = promoteResponse;
    }

    public Piece getPiece() {
        return piece;
    }

    public ImageView getImage() {
        return image;
    }

    public GUIPiece(Piece piece, ImageView image, GameView board) {

        this.piece = piece;
        this.image = image;
        this.b = board;

        this.promoteResponse = '0';

    }

    private void setPieceX(double x) {

        double offset = b.getBoard().getBoardBounds().getMinX();

        double ax = x - (b.getBoard().getPieceSize() / 2.0) - offset;

        if (x > b.getBoard().getBoardBounds().getMinX() && x < b.getBoard().getBoardBounds().getMaxX())
            image.setLayoutX(ax);
        else if (x <= b.getBoard().getBoardBounds().getMinX())
            image.setLayoutX(
                    b.getBoard().getBoardBounds().getMinX() - (b.getBoard().getPieceSize() / 2.0) - offset + 1);
        else if (x >= b.getBoard().getBoardBounds().getMaxX())
            image.setLayoutX(
                    b.getBoard().getBoardBounds().getMaxX() - (b.getBoard().getPieceSize() / 2.0) - offset - 1);

    }

    private void setPieceY(double y) {

        double offset = b.getBoard().getBoardBounds().getMinY();

        double ay = y - (b.getBoard().getPieceSize() / 2.0) - offset;

        if (y >= b.getBoard().getBoardBounds().getMinY() && y <= b.getBoard().getBoardBounds().getMaxY())
            image.setLayoutY(ay);
        else if (y <= b.getBoard().getBoardBounds().getMinY())
            image.setLayoutY(
                    b.getBoard().getBoardBounds().getMinY() - (b.getBoard().getPieceSize() / 2.0) - offset + 1);
        else if (y >= b.getBoard().getBoardBounds().getMaxY())
            image.setLayoutY(
                    b.getBoard().getBoardBounds().getMaxY() - (b.getBoard().getPieceSize() / 2.0) - offset - 1);

    }

    private void setPieceSquare(Square sq) {

        double x = b.getBoard().getXBySquare(sq);
        double ax = x + ((b.getBoard().getSquareSize() - b.getBoard().getPieceSize()) / 2.0);

        double y = b.getBoard().getYBySquare(sq);
        double ay = y + ((b.getBoard().getSquareSize() - b.getBoard().getPieceSize()) / 2.0);

        image.setLayoutX(ax);
        image.setLayoutY(ay);

    }

    /**
     * When the mouse has been pressed down.
     * 
     * @param ev The event of the mouse being pressed down.
     */
    public void onMousePressed(MouseEvent ev) {
        Square clickSquare = b.getBoard().getSquareByLoc(ev.getSceneX(), ev.getSceneY(), true);

        if (b.getBoard().getActive() != null
                && b.getGame().getLastPos().canPieceMoveToSquare(b.getBoard().getActive().getPiece(),
                        clickSquare)) {

            int cPos = b.getGame().getPositions().size() - 1;

            try {

                GUIPiece active = b.getBoard().getActive();

                Move m = new Move(b.getBoard().getActive().getPiece().getSquare(),
                        clickSquare, b.getGame().getLastPos());

                b.getBoard().setDragging(null);
                b.getBoard().setActive(null);

                if (b.isTurn()) {

                    promoteResponse = '0';

                    if (b.getGame().getLastPos().getMoves().contains(m) && m.getPromoteType() == '?') {

                        promoteMove = m;

                        Runnable callback = () -> {

                            try {

                                b.getBoard().showPromoteDialog(m.getDestination(), m.isWhite(), this);

                            } catch (Exception e) {

                            }

                        };

                        ArrayList<TranslateTransition> ts = b.getBoard().getPiecePane().getTransitions();
                        ts.clear();
                        // b.getChildren().remove(active.getImage());

                        if (m.getCapturePiece() != null)
                            b.getBoard().getPiecePane().getChildren()
                                    .remove(b.getBoard().getGUIPieceAtSquare(m.getCaptureSquare()).getImage());

                        active.setPieceSquare(clickSquare);
                        b.getBoard().getPiecePane().pieceMoveAnimation(active, m.getOrigin(), m.getDestination(),
                                m.getCapturePiece(),
                                callback);
                        for (TranslateTransition t : ts) {

                            t.play();

                        }

                    } else
                        b.getGame().makeMove(m.getOrigin(), m.getDestination(), '0');

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (promoteMove == null && cPos == b.getCurrentPos()) {
                GUIPiece pc = b.getBoard().getGUIPieceAtSquare(clickSquare);
                if (pc != null)
                    b.getBoard().setActive(pc);
                else
                    b.getBoard().setActive(null);

                b.getBoard().setDragging(null);

                setPieceSquare(piece.getSquare());

            }

        } else if (b.getBoard().getActive() == null
                || (!b.getGame().getPositions().get(b.getCurrentPos()).canPieceMoveToSquare(
                        b.getBoard().getActive().getPiece(),
                        clickSquare) && !b.getBoard().getActive().getPiece().equals(this.getPiece()))
                ||
                clickSquare.equals(piece.getSquare())) {

            image.toFront();
            b.getBoard().setActive(this);
            b.getBoard().setDragging(this);

            b.getBoard().activeUpdated();

            setPieceX(ev.getSceneX());
            setPieceY(ev.getSceneY());

            Square sq = b.getBoard().getSquareByLoc(image.getLayoutX() + (b.getBoard().getPieceSize() / 2.0),
                    image.getLayoutY() + (b.getBoard().getPieceSize() / 2.0), false);

            b.getBoard().getBorderPane().drawBorder(sq);

        } else {

            b.getBoard().setActive(null);
            b.getBoard().setDragging(null);

            b.getBoard().activeUpdated();
            b.getBoard().getBorderPane().drawBorder(null);

        }

    }

    /**
     * When the mouse has been dragged (with the mouse pressed down.)
     * 
     * @param ev The event of the mouse being dragged.
     */
    public void onMouseDragged(MouseEvent ev) {

        if (promoteMove != null)
            return;

        setPieceX(ev.getSceneX());
        setPieceY(ev.getSceneY());

        Square sq = b.getBoard().getSquareByLoc(image.getLayoutX() + (b.getBoard().getPieceSize() / 2.0),
                image.getLayoutY() + (b.getBoard().getPieceSize() / 2.0), false);

        b.getBoard().getBorderPane().drawBorder(sq);

    }

    /**
     * When the mouse has been lifted up.
     * 
     * @param ev The event of the mouse being lifted up.
     */
    public void onMouseReleased(MouseEvent ev) {

        if (promoteMove != null)
            return;

        b.getBoard().getBorderPane().drawBorder(null);

        if (b.getBoard().getDragging() == null && (b.getBoard().getActive() == null
                || (b.getBoard().getActive() != null && b.getBoard().getActive().getPiece().getSquare()
                        .equals(b.getBoard().getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY(), true))))) {

            b.getBoard().setDragging(null);

            b.getBoard().activeUpdated();

            return;

        } else if (b.getBoard().getActive() != null && b.getBoard().getDragging() != null
                && b.getBoard().getActive().getPiece().getSquare()
                        .equals(b.getBoard().getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY(), true))) {

            b.getBoard().setDragging(null);

            setPieceSquare(piece.getSquare());

            return;

        }

        if (b.getBoard().getDragging() != null) {

            int cPos = b.getCurrentPos();

            try {

                Piece d = b.getBoard().getDragging().getPiece();

                b.getBoard().setActive(null);

                Move m = new Move(d.getSquare(),
                        b.getBoard().getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY(), true),
                        b.getGame().getPositions().get(b.getCurrentPos()));

                if (b.isTurn()) {

                    promoteResponse = '0';

                    if (b.getGame().getLastPos().getMoves().contains(m) && m.getPromoteType() == '?') {

                        promoteMove = m;
                        GUIPiece capPiece = b.getBoard().getGUIPieceAtSquare(m.getCaptureSquare());

                        if (capPiece != null)
                            b.getBoard().getPiecePane().getChildren().remove(capPiece.getImage());

                        setPieceSquare(m.getDestination());
                        b.getBoard().showPromoteDialog(m.getDestination(), m.isWhite(), this);

                    } else
                        b.getGame().makeMove(m.getOrigin(), m.getDestination(), '0');

                }

            } catch (Exception e) {
                if (promoteMove == null && cPos == b.getCurrentPos()) {

                    GUIPiece pc = b.getBoard().getGUIPieceAtSquare(
                            b.getBoard().getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY(), true));

                    if (pc != null)
                        b.getBoard().setActive(pc);
                    else
                        b.getBoard().setActive(null);

                    b.getBoard().setDragging(null);
                    setPieceSquare(piece.getSquare());

                }
            }

        } else
            setPieceSquare(piece.getSquare());

    }

}