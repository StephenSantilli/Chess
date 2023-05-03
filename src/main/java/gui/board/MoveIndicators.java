package gui.board;

import java.util.ArrayList;

import game.*;
import gui.GameView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;

public class MoveIndicators extends Pane {

    private GameView gameView;
    private ArrayList<Square> moveSquares;

    public ArrayList<Square> getMoveSquares() {
        return moveSquares;
    }

    public MoveIndicators(GameView gameView) {
        this.gameView = gameView;
        moveSquares = new ArrayList<>();
    }

    public void clear() {
        getChildren().clear();
        moveSquares.clear();
    }

    public void draw() {

        getChildren().clear();
        moveSquares.clear();

        final Board board = gameView.getBoard();
        final double squareSize = board.getSquareSize();

        if (gameView.getGame() == null
                || board == null
                || board.getActive() == null
                || !gameView.isTurn()
                || gameView.getGame().getResult() != Game.Result.IN_PROGRESS)
            return;

        ArrayList<Move> pMoves = gameView.getGame().getLastPos().getMoves();

        for (Move m : pMoves) {

            if (!m.getPiece().equals(board.getActive().getPiece()))
                continue;

            final Square dest = m.isCastle() ? m.getRookOrigin() : m.getDestination();
            moveSquares.add(dest);

            final double x = board.getXBySquare(dest, false);
            final double y = board.getYBySquare(dest, false);

            if ((m.isCapture() && m.getCaptureSquare().equals(dest)) || m.isCastle()) {

                Ellipse captureCircle = new Ellipse((squareSize - (squareSize * .1)) / 2.0,
                        (squareSize - (squareSize * .1)) / 2.0);

                captureCircle.setId("captureCircle");
                captureCircle.setStrokeWidth(squareSize * 0.04);

                captureCircle.setLayoutX(x + (squareSize / 2.0));
                captureCircle.setLayoutY(y + (squareSize / 2.0));

                getChildren().add(captureCircle);

            } else {

                Ellipse nonCaptureCircle = new Ellipse(squareSize / 6.0, squareSize / 6.0);

                nonCaptureCircle.setId("nonCaptureCircle");

                nonCaptureCircle.setLayoutX(x + (squareSize / 2.0));
                nonCaptureCircle.setLayoutY(y + (squareSize / 2.0));

                getChildren().add(nonCaptureCircle);

            }
        }

    }

}
