package gui.board;

import java.util.ArrayList;

import game.Position;
import game.Square;
import game.pieces.Piece;
import gui.GameView;
import gui.PieceTranscoder;
import gui.board.element.GUIPiece;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * The pane which contains the piece images.
 */
public class Pieces extends Pane {

    /**
     * The GameView that contains this pane.
     */
    private GameView gameView;

    /**
     * The pieces that are part of this pane.
     */
    private ArrayList<GUIPiece> pieces;

    /**
     * The transcoders that are used to draw the pieces.
     */
    private ArrayList<PieceTranscoder> transcoders;

    /**
     * The list of pending piece animations.
     */
    private ArrayList<TranslateTransition> transitions;

    /**
     * Creates a new pieces pane.
     * 
     * @param gameView The GameView that contains this pane.
     */
    public Pieces(GameView gameView) {
        this.gameView = gameView;
        this.transitions = new ArrayList<TranslateTransition>();
    }

    /**
     * Gets the pieces.
     * 
     * @return {@link #pieces}
     */
    public ArrayList<GUIPiece> getPieces() {
        return pieces;
    }

    /**
     * Gets the transcoders used to display the pieces.
     * 
     * @return {@link #transcoders}
     */
    public ArrayList<PieceTranscoder> getTranscoders() {
        return transcoders;
    }

    /**
     * Gets the list of the pending piece animations.
     * 
     * @return {@link #transitions}
     */
    public ArrayList<TranslateTransition> getTransitions() {
        return transitions;
    }

    /**
     * Clears the old pieces and draws the pieces on the board in the most recent
     * position.
     */
    public void draw() {
        draw(false, null, null);
    }

    /**
     * Clears the old pieces and draws the pieces on the board.
     * 
     * @param backward {@code true} if {@code p2} is before {@code p1} (such as when
     *                 undoing.)
     * @param prev     The position before the board was updated. If {@code null},
     *                 the position change will not be animated.
     * @param curr     The position after the board was updated. If {@code null},
     *                 the active position of the game will be used instead.
     */
    public void draw(boolean backward, Position prev, Position curr) {

        final Board board = gameView.getBoard();
        final double squareSize = board.getSquareSize();
        final double pieceSize = board.getPieceSize();

        this.pieces = new ArrayList<GUIPiece>();
        transitions = new ArrayList<TranslateTransition>();

        getChildren().clear();

        if (gameView.getGame() == null)
            return;

        if (curr == null)
            curr = gameView.getGame().getPositions().get(gameView.getCurrentPos());

        for (int r = 0; r < 8; r++) {

            for (int c = 0; c < 8; c++) {

                Piece p = curr.getPieceAtSquare(new Square(r + 1, c + 1));

                if (p == null)
                    continue;

                ImageView img = getPieceTranscoder(p).toImageView();
                GUIPiece guiP = new GUIPiece(p, img, gameView);

                getChildren().add(img);

                pieces.add(guiP);

                img.setLayoutX(board.getXBySquare(p.getSquare()) + ((squareSize - pieceSize) / 2.0));
                img.setLayoutY(board.getYBySquare(p.getSquare()) + ((squareSize - pieceSize) / 2.0));

                if (prev != null && curr != null
                        && ((!backward && curr.getMove() != null) || (backward && prev.getMove() != null))
                        // Either not backwards and the piece in the move of curr is the piece @ this
                        // square
                        && ((!backward && curr.getMove().getDestination().equals(p.getSquare()))
                                // Or it is backwards and the piece in the move of prev is this piece
                                || (backward && prev.getMove().getOrigin().equals(p.getSquare()))
                                // Same as first term, but for castle moves.
                                || (!backward && curr.getMove().isCastle() && p.getCode() == 'R'
                                        && curr.getMove().getRookDestination().equals(p.getSquare()))
                                // Same as second term, but for castle moves.
                                || (backward && prev.getMove().isCastle() && p.getCode() == 'R'
                                        && prev.getMove().getRookOrigin().equals(p.getSquare())))) {

                    if (!backward) {

                        // Castle move
                        if (curr.getMove().isCastle() && p.getCode() == 'R'
                                && curr.getMove().getRookDestination().equals(p.getSquare()))
                            pieceMoveAnimation(guiP,
                                    curr.getMove().getRookOrigin(),
                                    curr.getMove().getRookDestination(),
                                    null);
                        // Regular move
                        else
                            pieceMoveAnimation(guiP,
                                    curr.getMove().getOrigin(),
                                    curr.getMove().getDestination(),
                                    curr.getMove().getCapturePiece());

                    } else {

                        // Castle move
                        if (prev.getMove().isCastle() && p.getCode() == 'R'
                                && prev.getMove().getRookOrigin().equals(p.getSquare()))
                            pieceMoveAnimation(guiP,
                                    prev.getMove().getRookDestination(),
                                    prev.getMove().getRookOrigin(),
                                    null);
                        // Regular move
                        else
                            pieceMoveAnimation(guiP,
                                    prev.getMove().getDestination(),
                                    prev.getMove().getOrigin(),
                                    prev.getMove().getCapturePiece());

                    }

                }

            }
        }

        for (TranslateTransition t : transitions) {
            t.play();
        }

    }

    /**
     * Creates and plays an animation of a piece moving. If {@code capture} is not
     * {@code null}, the capture
     * piece will still show up until the animation completes.
     * 
     * @param guiPiece    The piece to animate
     * @param origin      The start square of the animated piece
     * @param destination The end square of the animated piece
     * @param capture     The piece captured by {@code guiPiece}. Should be
     *                    {@code null} if
     *                    there is no piece being captured.
     */
    public void pieceMoveAnimation(GUIPiece guiPiece, Square origin, Square destination, Piece capture) {
        pieceMoveAnimation(guiPiece, origin, destination, capture, null);
    }

    /**
     * Creates and plays an animation of a piece moving. If {@code capture} is not
     * {@code null}, the capture piece will still show up until the animation
     * completes.
     * 
     * @param guiPiece    The piece to animate
     * @param origin      The start square of the animated piece
     * @param destination The end square of the animated piece
     * @param capture     The piece captured by {@code guiPiece}. Should be
     *                    {@code null} if
     *                    there is no piece being captured.
     * @param callback    A callback to be exctued when the animation is complete.
     */
    public void pieceMoveAnimation(GUIPiece guiPiece, Square origin, Square destination, Piece capture,
            Runnable callback) {

        final Board board = gameView.getBoard();
        final double squareSize = board.getSquareSize();
        final double pieceSize = board.getPieceSize();

        ImageView img = guiPiece.getImage();

        TranslateTransition t = new TranslateTransition(Duration.millis(150), img);

        double fromX = board.getXBySquare(origin) + ((squareSize - pieceSize) / 2.0);
        double fromY = board.getYBySquare(origin) + ((squareSize - pieceSize) / 2.0);

        double toX = board.getXBySquare(destination) + ((squareSize - pieceSize) / 2.0);
        double toY = board.getYBySquare(destination) + ((squareSize - pieceSize) / 2.0);

        t.setFromX(fromX - toX);
        t.setFromY(fromY - toY);

        t.setToX(0);
        t.setToY(0);

        if (capture != null) {

            ImageView i = getPieceTranscoder(capture).toImageView();

            getChildren().add(i);

            GUIPiece guiP = new GUIPiece(capture, i, gameView);

            i.setLayoutX(board.getXBySquare(capture.getSquare()) + ((squareSize - pieceSize) / 2.0));
            i.setLayoutY(((board.getYBySquare(capture.getSquare()))) + ((squareSize - pieceSize) / 2.0));

            t.getNode().toFront();

            t.setOnFinished(e -> {

                getChildren().remove(guiP.getImage());

                if (callback != null)
                    callback.run();

            });

        } else {

            t.setOnFinished(e -> {

                if (callback != null)
                    callback.run();

            });

        }

        transitions.add(t);

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
     * Initializes the piece transcoders based on the current size of the pieces.
     * 
     * @throws Exception If there is an error transcoding the SVGs.
     */
    public void initPieceTranscoders() throws Exception {

        final Board board = gameView.getBoard();
        final double pieceSize = board.getPieceSize();

        transcoders = new ArrayList<PieceTranscoder>();

        boolean color = true;

        for (int i = 0; i < 2; i++) {

            transcoders.add(new PieceTranscoder(pieceSize, color, 'K'));
            transcoders.add(new PieceTranscoder(pieceSize, color, 'Q'));
            transcoders.add(new PieceTranscoder(pieceSize, color, 'R'));
            transcoders.add(new PieceTranscoder(pieceSize, color, 'B'));
            transcoders.add(new PieceTranscoder(pieceSize, color, 'N'));
            transcoders.add(new PieceTranscoder(pieceSize, color, 'P'));

            color = false;

        }

    }

}
