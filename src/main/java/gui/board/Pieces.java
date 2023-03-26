package gui.board;

import java.util.ArrayList;

import game.Position;
import game.Square;
import game.pieces.Piece;
import gui.PieceTranscoder;
import gui.component.GameView;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Pieces extends Pane {

    private GameView gameView;

    private ArrayList<GUIPiece> pieces;
    private ArrayList<PieceTranscoder> transcoderPieces;
    private ArrayList<TranslateTransition> transitions;

    public ArrayList<GUIPiece> getPieces() {
        return pieces;
    }

    public ArrayList<PieceTranscoder> getTranscoderPieces() {
        return transcoderPieces;
    }

    public ArrayList<TranslateTransition> getTransitions() {
        return transitions;
    }

    public Pieces(GameView gameView) {
        this.gameView = gameView;
        this.transitions = new ArrayList<TranslateTransition>();
    }

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

                ImageView img = getPieceTranscoder(p).getImageView();
                GUIPiece guiP = new GUIPiece(p, img, gameView);

                getChildren().add(img);

                pieces.add(guiP);

                img.setLayoutX(board.getXBySquare(p.getSquare()) + ((squareSize - pieceSize) / 2.0));
                img.setLayoutY(board.getYBySquare(p.getSquare()) + ((squareSize - pieceSize) / 2.0));

                if (prev != null && curr != null
                        && ((!backward && curr.getMove() != null) || (backward && prev.getMove() != null))
                        // Either not backwards and the piece in the move of p2 is this piece
                        && ((!backward && curr.getMove().getDestination().equals(p.getSquare()))
                                // Or it is backwards and the piece in the move of p1 is this piece
                                || (backward && prev.getMove().getOrigin().equals(p.getSquare()))
                                // Same as first term, but for castle moves.
                                || (!backward && curr.getMove().isCastle()
                                        && curr.getMove().getRookDestination().equals(p.getSquare()))
                                // Same as second term, but for castle moves.
                                || (backward && prev.getMove().isCastle()
                                        && prev.getMove().getRookOrigin().equals(p.getSquare())))) {

                    if (!backward) {

                        if (curr.getMove().isCastle() && curr.getMove().getRookDestination().equals(p.getSquare()))
                            pieceMoveAnimation(guiP, curr.getMove().getRookOrigin(),
                                    curr.getMove().getRookDestination(),
                                    null);
                        else
                            pieceMoveAnimation(guiP, curr.getMove().getOrigin(), curr.getMove().getDestination(),
                                    curr.getMove().getCapturePiece());

                    } else {

                        if (prev.getMove().isCastle() && prev.getMove().getRookOrigin().equals(p.getSquare()))
                            pieceMoveAnimation(guiP, prev.getMove().getRookDestination(),
                                    prev.getMove().getRookOrigin(),
                                    null);
                        else
                            pieceMoveAnimation(guiP, prev.getMove().getDestination(), prev.getMove().getOrigin(),
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

            ImageView i = getPieceTranscoder(capture).getImageView();

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

        for (int i = 0; i < transcoderPieces.size() && found == null; i++) {

            PieceTranscoder pt = transcoderPieces.get(i);
            if (pt.isColor() == piece.isWhite() && pt.getPieceCode() == piece.getCode())
                found = pt;

        }

        return found;

    }

    public void initPieceTranscoders() throws Exception {

        final Board board = gameView.getBoard();
        final double pieceSize = board.getPieceSize();

        transcoderPieces = new ArrayList<PieceTranscoder>();

        boolean color = true;

        System.out.println(pieceSize);

        for (int i = 0; i < 2; i++) {

            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'K'));
            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'Q'));
            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'R'));
            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'B'));
            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'N'));
            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'P'));

            color = false;

        }

    }

}
