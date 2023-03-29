package gui.board;

import game.Square;
import gui.GameView;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class Coordinates extends Pane {

    private GameView gameView;

    public Coordinates(GameView gameView) {
        this.gameView = gameView;
    }

    public void draw() {

        final Board board = gameView.getBoard();

        getChildren().clear();

        final double squareSize = board.getSquareSize();

        for (int r = 8; r > 0; r--) {

            final Square square = new Square(1, r);
            final double inset = board.getSquareSize() / 20;
            Label l = new Label(r + "");
            l.setLayoutX(board.getXBySquare(square) + inset);
            l.setLayoutY(board.getYBySquare(square) + inset);

            l.setId(square.isLightSquare() ? "lightCoord" : "darkCoord");

            getChildren().add(l);

        }

        for (int f = 1; f <= 8; f++) {

            final Square square = new Square(f, 1);
            // final double inset = (board.getSquareSize() / 20) * 16;
            final double inset = board.getSquareSize() / 20;

            Label l = new Label((char)(f + 96) + "");
            l.setLayoutX((board.getXBySquare(square) + (inset)));
            l.setLayoutY((board.getYBySquare(square) + (inset)));

            l.setId(square.isLightSquare() ? "lightCoord" : "darkCoord");

            getChildren().add(l);

        }

    }

}
