package gui.board;

import game.Square;
import gui.component.GameView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class Squares extends Pane {

    private GameView gameView;

    public Squares(GameView gameView) {
        this.gameView = gameView;
    }

    public void draw() {

        final Board board = gameView.getBoard();

        getChildren().clear();

        final double squareSize = board.getSquareSize();

        for (int r = 8; r > 0; r--) {

            for (int f = 8; f > 0; f--) {

                Region sq = new Region();
                sq.setPrefSize(squareSize, squareSize);
                sq.setMinSize(squareSize, squareSize);

                Square square = new Square(f, r);

                sq.setLayoutX(board.getXBySquare(square));
                sq.setLayoutY(board.getYBySquare(square));

                if (square.isLightSquare())
                    sq.setId("lightSquare");
                else
                    sq.setId("darkSquare");

                String radius = Board.getSquareCornerRadius(square);

                sq.setStyle("-fx-background-radius: " + radius);

                getChildren().add(sq);

            }

        }

    }

}
