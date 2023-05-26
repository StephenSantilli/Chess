package gui.board.element;

import game.Game;
import game.Move;
import game.Square;
import game.pieces.Piece;

import gui.GameView;
import gui.board.Board;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * A visual representation of a piece in a chess game.
 */
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

    /**
     * If the piece has been the active piece. If {@code true}, the next time it is
     * released it should be deselected.
     */
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

    /**
     * The callback to be ran when the user has made a promote decision.
     */
    private Runnable promoteCallback = () -> {

        if (promoteMove == null || promoteResponse == '0')
            return;

        if (promoteResponse == 'X') {

            gameView.getBoard().draw();
            promoteResponse = '0';
            promoteMove = null;

        } else {

            try {

                gameView.getGame().makeMove(promoteMove.getOrigin(), promoteMove.getDestination(), promoteResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    /**
     * Creates a new piece to be displayed in the GUI based on a {@link Piece}
     * object.
     * 
     * @param piece    The piece to represent.
     * @param image    The image that matches the piece.
     * @param gameView The game view this piece is apart of.
     */
    public GUIPiece(Piece piece, ImageView image, GameView gameView) {

        this.piece = piece;
        this.image = image;
        this.gameView = gameView;

        this.promoteResponse = '0';

        alreadyActive = false;

    }

    /**
     * Gets the promote move.
     * 
     * @return {@link #promoteMove}
     */
    public Move getPromoteMove() {
        return promoteMove;
    }

    /**
     * Sets {@link #promoteMove}.
     * 
     * @param promoteMove The promote move.
     */
    public void setPromoteMove(Move promoteMove) {
        this.promoteMove = promoteMove;
    }

    /**
     * Gets the promotion callback.
     * 
     * @return {@link #promoteCallback}
     */
    public Runnable getPromoteCallback() {
        return promoteCallback;
    }

    /**
     * Gets the promotion response.
     * 
     * @return {@link #promoteResponse}
     */
    public char getPromoteResponse() {
        return promoteResponse;
    }

    /**
     * Sets {@link #promoteResponse}.
     * 
     * @param promoteResponse The promote response.
     */
    public void setPromoteResponse(char promoteResponse) {
        this.promoteResponse = promoteResponse;
    }

    /**
     * Gets the piece.
     * 
     * @return {@link #piece}
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Gets the image that displays the piece.
     * 
     * @return {@link #image}
     */
    public ImageView getImage() {
        return image;
    }

    /**
     * Gets whether or not the piece is already active.
     * 
     * @return {@link #alreadyActive}
     */
    public boolean isAlreadyActive() {
        return alreadyActive;
    }

    /**
     * Sets {@link #alreadyActive}.
     * 
     * @param alreadyActive If already active.
     */
    public void setAlreadyActive(boolean alreadyActive) {
        this.alreadyActive = alreadyActive;
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

        if (game == null || game.getPositions().size() == 0)
            return;

        // If there's an active piece and it can move to the square clicked
        if (board.getActive() != null
                && game.getLastPos().findMove(board.getActive().getPiece().getSquare(), targetSquare) != null) {

            final Move move = game.getLastPos().findMove(board.getActive().getPiece().getSquare(), targetSquare);

            try {

                final GUIPiece active = board.getActive();

                board.setDragging(null);
                board.setActive(null);

                if (gameView.isTurn()) {

                    promoteResponse = '0';

                    // If this move is a promotion
                    if (move.getPromoteType() == '?') {

                        promoteMove = move;

                        Runnable callback = () -> {

                            try {

                                // board.getPiecePane().getTransitions().clear();
                                board.showPromoteDialog(move.getDestination(), move.isWhite(), this);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        };

                        ArrayList<TranslateTransition> transitions = board.getPiecePane().getTransitions();
                        transitions.clear();

                        // If promote move is a capture
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
                        // Not a promote move
                        game.makeMove(move.getOrigin(), targetSquare,
                                '0');

                }

            } catch (Exception e) {

                if (promoteMove == null) {

                    GUIPiece pc = board.getGUIPieceAtSquare(targetSquare);

                    board.setDragging(null);
                    if (pc != null)
                        board.setActive(pc);
                    else
                        board.setActive(null);

                    setPieceSquare(piece.getSquare());

                }

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

            board.setDragging(this);
            board.setActive(this);

            setPieceX(ev.getSceneX());
            setPieceY(ev.getSceneY());

            Square sq = board.getSquareByPoint(
                    image.getLayoutX() + (board.getPieceSize() / 2.0),
                    image.getLayoutY() + (board.getPieceSize() / 2.0), false);

            board.getBorderPane().drawBorder(sq);

        } else {

            board.setDragging(null);
            board.setActive(null);

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
                image.getLayoutY() + (gameView.getBoard().getPieceSize() / 2.0),
                false);

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

        final Game game = gameView.getGame();
        final Board board = gameView.getBoard();
        final Square targetSquare = board.getSquareByPoint(ev.getSceneX(), ev.getSceneY(), true);

        board.getBorderPane().drawBorder(null);

        if (game == null || game.getPositions().size() == 0)
            return;

        final GUIPiece active = board.getActive();

        // No active piece or the target square is already the piece's square
        if ((board.getActive() == null
                || (board.getActive() != null && board.getActive().getPiece().getSquare()
                        .equals(targetSquare)))) {

            board.setDragging(null);

            if (active != null && active.isAlreadyActive())
                board.setActive(null);
            else if (active != null)
                active.setAlreadyActive(true);

            setPieceSquare(piece.getSquare());

            return;

        } else {

            if (active != null)
                active.setAlreadyActive(true);

        }

        // There's a piece that was being dragged
        if (board.getDragging() != null) {

            try {

                final Piece dragPiece = board.getDragging().getPiece();

                final Move move = game.getLastPos().findMove(dragPiece.getSquare(), targetSquare);

                if (gameView.isTurn() && move != null) {

                    promoteResponse = '0';

                    // If promote move
                    if (move.getPromoteType() == '?') {

                        promoteMove = move;
                        GUIPiece capPiece = board.getGUIPieceAtSquare(move.getCaptureSquare());

                        if (capPiece != null)
                            board.getPiecePane().getChildren().remove(capPiece.getImage());

                        setPieceSquare(move.getDestination());
                        board.getMoveIndicatorsPane().clear();
                        board.showPromoteDialog(move.getDestination(), move.isWhite(), this);

                    } else
                        game.makeMove(move.getOrigin(), move.getDestination(), '0');

                } else {
                    board.draw();
                }

            } catch (Exception e) {

                if (promoteMove == null) {

                    board.setDragging(null);

                    setPieceSquare(piece.getSquare());

                }

            }

        } else
            setPieceSquare(piece.getSquare());

    }

    /**
     * Sets the x value of a piece, keeping it within the bounds of the board.
     * 
     * <p>
     * Useful for dragging the piece, as it won't allow the piece to go outside of
     * the edges.
     * 
     * @param x The x location to set the piece to.
     */
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

    /**
     * Sets the y value of a piece, keeping it within the bounds of the board.
     * 
     * <p>
     * Useful for dragging the piece, as it won't allow the piece to go outside of
     * the edges.
     * 
     * @param y The y location to set the piece to.
     */
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

    /**
     * Sets the piece's layout position based on the given square.
     * 
     * @param square The square to set the piece to.
     */
    private void setPieceSquare(Square square) {

        double x = gameView.getBoard().getXBySquare(square);
        double ax = x + ((gameView.getBoard().getSquareSize() - gameView.getBoard().getPieceSize()) / 2.0);

        double y = gameView.getBoard().getYBySquare(square);
        double ay = y + ((gameView.getBoard().getSquareSize() - gameView.getBoard().getPieceSize()) / 2.0);

        image.setLayoutX(ax);
        image.setLayoutY(ay);

    }

}