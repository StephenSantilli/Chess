package gui.board;

import java.util.ArrayList;

import game.Game;
import game.Move;
import gui.GameView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;

public class MoveIndicators extends Pane {

    private GameView gameView;

    public MoveIndicators(GameView gameView) {
        this.gameView = gameView;
    }

    public void draw() {

        getChildren().clear();

        final Board board = gameView.getBoard();
        final double squareSize = board.getSquareSize();

        if (gameView.getGame() == null
                || board == null
                || board.getActive() == null
                || !gameView.isTurn()
                || gameView.getGame().getResult() != Game.RESULT_IN_PROGRESS)
            return;

        ArrayList<Move> pMoves = gameView.getGame().getLastPos().getMoves();

        for (Move m : pMoves) {

            if (!m.getPiece().equals(board.getActive().getPiece()))
                continue;

            final double x = board.getXBySquare(m.getDestination(), false);
            final double y = board.getYBySquare(m.getDestination(), false);

            if (m.isCapture() && m.getCaptureSquare().equals(m.getDestination())) {

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
