package gui.board;

import game.Position;
import game.Square;
import gui.GameView;
import javafx.scene.layout.Pane;

public class Highlights extends Pane {

    private GameView gameView;

    public Highlights(GameView gameView) {
        this.gameView = gameView;
    }

    public void draw() {

        getChildren().clear();

        final Board board = gameView.getBoard();

        if (gameView.getGame() == null)
            return;

        if (gameView.getCurrentPos() > 0) {

            final Position pos = gameView.getGame().getPositions().get(gameView.getCurrentPos());

            final Square origin = pos.getMove().getOrigin();

            Pane oRect = new Pane();
            oRect.setId("moveSquare");

            oRect.setMinSize(board.getSquareSize(), board.getSquareSize());
            oRect.setPrefSize(board.getSquareSize(), board.getSquareSize());

            oRect.setLayoutX(board.getXBySquare(origin));
            oRect.setLayoutY(board.getYBySquare(origin));

            oRect.setStyle("-fx-background-radius: " + Board.getSquareCornerRadius(origin));

            final Square destination = pos.getMove().getDestination();

            Pane dRect = new Pane();
            dRect.setId("moveSquare");

            dRect.setMinSize(board.getSquareSize(), board.getSquareSize());
            dRect.setPrefSize(board.getSquareSize(), board.getSquareSize());

            dRect.setLayoutX(board.getXBySquare(destination));
            dRect.setLayoutY(board.getYBySquare(destination));

            dRect.setStyle("-fx-background-radius: " + Board.getSquareCornerRadius(destination));

            getChildren().addAll(oRect, dRect);

        }

        if (board.getDragging() != null || board.getActive() != null) {

            final Square aSquare = board.getDragging() != null ? board.getDragging().getPiece().getSquare()
                    : board.getActive().getPiece().getSquare();

            Pane aRect = new Pane();
            aRect.setId("activeSquare");

            aRect.setMinSize(board.getSquareSize(), board.getSquareSize());
            aRect.setPrefSize(board.getSquareSize(), board.getSquareSize());

            aRect.setLayoutX(board.getXBySquare(aSquare));
            aRect.setLayoutY(board.getYBySquare(aSquare));

            aRect.setStyle("-fx-background-radius: " + Board.getSquareCornerRadius(aSquare));

            getChildren().addAll(aRect);

        }

    }

}
