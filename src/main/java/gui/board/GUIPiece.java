package gui.board;

import java.util.ArrayList;

import game.Game;
import game.Move;
import game.Square;
import game.pieces.Piece;
import gui.GameView;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class GUIPiece {

    /** The {@link Piece} that this node represents. */
    private Piece piece;

    /**
     * The image that displays the piece.
     */
    private ImageView image;

    /**
     * The {@link GameView} that this piece is displayed on.
     */
    private GameView gameView;

    private boolean alreadyActive;

    /**
     * The response (piece code) of the promote dialog. Should be {@code 0} if no
     * promotion to
     * occur.
     */
    private char promoteResponse;

    /**
     * The {@link Move} that will be made once the {@link #promoteResponse} has been
     * set.
     */
    private Move promoteMove;

    private Runnable promoteCallback = () -> {

        if (promoteMove == null || promoteResponse == '0')
            return;

        if (promoteResponse == 'X') {

            gameView.getBoard().draw();
            promoteResponse = '0';
            promoteMove = null;

        }

        else {

            try {
                gameView.getGame().makeMove(promoteMove.getOrigin(), promoteMove.getDestination(), promoteResponse);
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

    public boolean isAlreadyActive() {
        return alreadyActive;
    }

    public void setAlreadyActive(boolean alreadyActive) {
        this.alreadyActive = alreadyActive;
    }

    public GUIPiece(Piece piece, ImageView image, GameView board) {

        this.piece = piece;
        this.image = image;
        this.gameView = board;

        this.promoteResponse = '0';

        alreadyActive = false;

    }

    private void setPieceX(double x) {

        final double offset = gameView.getBoard().getBoardBounds().getMinX();
        final double ax = x - (gameView.getBoard().getPieceSize() / 2.0) - offset;

        if (x > gameView.getBoard().getBoardBounds().getMinX() && x < gameView.getBoard().getBoardBounds().getMaxX())
            image.setLayoutX(ax);
        else if (x <= gameView.getBoard().getBoardBounds().getMinX())
            image.setLayoutX(
                    gameView.getBoard().getBoardBounds().getMinX() - (gameView.getBoard().getPieceSize() / 2.0) - offset
                            + 1);
        else if (x >= gameView.getBoard().getBoardBounds().getMaxX())
            image.setLayoutX(
                    gameView.getBoard().getBoardBounds().getMaxX() - (gameView.getBoard().getPieceSize() / 2.0) - offset
                            - 1);

    }

    private void setPieceY(double y) {

        final double offset = gameView.getBoard().getBoardBounds().getMinY();
        final double ay = y - (gameView.getBoard().getPieceSize() / 2.0) - offset;

        if (y >= gameView.getBoard().getBoardBounds().getMinY() && y <= gameView.getBoard().getBoardBounds().getMaxY())
            image.setLayoutY(ay);
        else if (y <= gameView.getBoard().getBoardBounds().getMinY())
            image.setLayoutY(
                    gameView.getBoard().getBoardBounds().getMinY() - (gameView.getBoard().getPieceSize() / 2.0) - offset
                            + 1);
        else if (y >= gameView.getBoard().getBoardBounds().getMaxY())
            image.setLayoutY(
                    gameView.getBoard().getBoardBounds().getMaxY() - (gameView.getBoard().getPieceSize() / 2.0) - offset
                            - 1);

    }

    private void setPieceSquare(Square sq) {

        double x = gameView.getBoard().getXBySquare(sq);
        double ax = x + ((gameView.getBoard().getSquareSize() - gameView.getBoard().getPieceSize()) / 2.0);

        double y = gameView.getBoard().getYBySquare(sq);
        double ay = y + ((gameView.getBoard().getSquareSize() - gameView.getBoard().getPieceSize()) / 2.0);

        image.setLayoutX(ax);
        image.setLayoutY(ay);

    }

    /**
     * When the mouse has been pressed down.
     * 
     * @param ev The event of the mouse being pressed down.
     */
    public void onMousePressed(MouseEvent ev) {

        final Square targetSquare = gameView.getBoard().getSquareByPoint(ev.getSceneX(), ev.getSceneY(), true);
        final Game game = gameView.getGame();
        final Board board = gameView.getBoard();

        // If there's an active piece and it can move to square that was clicked
        if (board.getActive() != null
                && game.getLastPos().canPieceMoveToSquare(board.getActive().getPiece(), targetSquare)) {

            // final int startPos = game.getPositions().size() - 1;

            try {

                final GUIPiece active = board.getActive();

                final Move move = new Move(board.getActive().getPiece().getSquare(), targetSquare, game.getLastPos());

                active.setAlreadyActive(false);

                board.setDragging(null);
                board.setActive(null);

                if (gameView.isTurn()) {

                    promoteResponse = '0';

                    // If this move is a promotion
                    if (game.getLastPos().getMoves().contains(move) && move.getPromoteType() == '?') {

                        promoteMove = move;

                        Runnable callback = () -> {

                            try {

                                board.showPromoteDialog(move.getDestination(), move.isWhite(), this);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        };

                        ArrayList<TranslateTransition> transitions = board.getPiecePane().getTransitions();
                        transitions.clear();

                        if (move.getCapturePiece() != null)
                            board.getPiecePane().getChildren()
                                    .remove(board.getGUIPieceAtSquare(move.getCaptureSquare()).getImage());

                        active.setPieceSquare(targetSquare);
                        board.getPiecePane().pieceMoveAnimation(active,
                                move.getOrigin(),
                                move.getDestination(),
                                move.getCapturePiece(),
                                callback);

                        for (TranslateTransition t : transitions) {
                            t.play();
                        }

                    } else
                        game.makeMove(move.getOrigin(), move.getDestination(), '0');

                }

            } catch (Exception e) {

                // If this wasn't a promote move, and the position never changed
                if (promoteMove == null/* && startPos == gameView.getCurrentPos() */) {

                    GUIPiece pc = board.getGUIPieceAtSquare(targetSquare);

                    board.getActive().setAlreadyActive(false);

                    if (pc != null)
                        board.setActive(pc);
                    else
                        board.setActive(null);

                    board.setDragging(null);

                    setPieceSquare(piece.getSquare());

                }

                // e.printStackTrace();

            }

            // if there's no active, the piece clicked cannot be captured, or the square
            // clicked is the active's square.
        } else if (board.getActive() == null
                || (!game.getPositions().get(gameView.getCurrentPos()).canPieceMoveToSquare(
                        board.getActive().getPiece(),
                        targetSquare)
                        && !board.getActive().getPiece().equals(this.getPiece()))
                || targetSquare.equals(piece.getSquare())) {

            image.toFront();

            board.getActive().setAlreadyActive(false);

            board.setActive(this);
            board.setDragging(this);

            board.activeUpdated();

            setPieceX(ev.getSceneX());
            setPieceY(ev.getSceneY());

            Square sq = board.getSquareByPoint(
                    image.getLayoutX() + (board.getPieceSize() / 2.0),
                    image.getLayoutY() + (board.getPieceSize() / 2.0), false);

            board.getBorderPane().drawBorder(sq);

        } else {

            board.getActive().setAlreadyActive(false);

            board.setActive(null);
            board.setDragging(null);

            board.activeUpdated();
            board.getBorderPane().drawBorder(null);

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

        Square sq = gameView.getBoard().getSquareByPoint(
                image.getLayoutX() + (gameView.getBoard().getPieceSize() / 2.0),
                image.getLayoutY() + (gameView.getBoard().getPieceSize() / 2.0), false);

        gameView.getBoard().getBorderPane().drawBorder(sq);

    }

    /**
     * When the mouse has been lifted up.
     * 
     * @param ev The event of the mouse being lifted up.
     */
    public void onMouseReleased(MouseEvent ev) {

        if (promoteMove != null)
            return;

        final Square targetSquare = gameView.getBoard().getSquareByPoint(ev.getSceneX(), ev.getSceneY(), true);
        final Game game = gameView.getGame();
        final Board board = gameView.getBoard();

        board.getBorderPane().drawBorder(null);

        // No active piece or the target square is already the piece's square
        if ((board.getActive() == null
                || (board.getActive() != null && board.getActive().getPiece().getSquare()
                        .equals(targetSquare)))) {

            if (alreadyActive) {
                board.setActive(null);
                alreadyActive = false;
            } else {
                alreadyActive = true;
            }

            board.setDragging(null);

            board.activeUpdated();
            setPieceSquare(piece.getSquare());

            return;

        } else {
            alreadyActive = true;
        }

        // There's a piece that was being dragged
        if (board.getDragging() != null) {

            // final int startPos = gameView.getCurrentPos();

            try {

                final Piece d = board.getDragging().getPiece();

                board.setActive(null);

                final Move move = new Move(d.getSquare(),
                        targetSquare,
                        game.getPositions().get(gameView.getCurrentPos()));

                if (gameView.isTurn()) {

                    promoteResponse = '0';

                    // If promote move
                    if (game.getLastPos().getMoves().contains(move) && move.getPromoteType() == '?') {

                        promoteMove = move;
                        GUIPiece capPiece = board.getGUIPieceAtSquare(move.getCaptureSquare());

                        if (capPiece != null)
                            board.getPiecePane().getChildren().remove(capPiece.getImage());

                        setPieceSquare(move.getDestination());
                        board.showPromoteDialog(move.getDestination(), move.isWhite(), this);

                    } else
                        game.makeMove(move.getOrigin(), move.getDestination(), '0');

                } else
                    board.draw();

            } catch (Exception e) {

                // If this wasn't a promote move, and the position never changed
                if (promoteMove == null/* && startPos == gameView.getCurrentPos() */) {

                    GUIPiece pc = board.getGUIPieceAtSquare(targetSquare);

                    if (pc != null)
                        board.setActive(pc);
                    else
                        board.setActive(null);

                    board.setDragging(null);

                    setPieceSquare(piece.getSquare());

                }

                // e.printStackTrace();

            }

        } else
            setPieceSquare(piece.getSquare());

    }

}