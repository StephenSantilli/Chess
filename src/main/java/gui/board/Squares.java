package gui.board;

import game.Square;
import gui.GameView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class Squares extends Pane {

    private GameView gameView;

    public Squares(GameView gameView) {
        this.gameView = gameView;
    }

    public void draw() {

        getChildren().clear();

        final Board board = gameView.getBoard();
        final double squareSize = board.getSquareSize();

        for (int r = 8; r > 0; r--) {

            for (int f = 8; f > 0; f--) {

                final Square square = new Square(f, r);
                final String radius = Board.getSquareCornerRadius(square);

                Region rect = new Region();

                if (square.isLightSquare())
                    rect.setId("lightSquare");
                else
                    rect.setId("darkSquare");

                rect.setMinSize(squareSize, squareSize);
                rect.setPrefSize(squareSize, squareSize);

                rect.setLayoutX(board.getXBySquare(square));
                rect.setLayoutY(board.getYBySquare(square));

                rect.setStyle("-fx-background-radius: " + radius + ";");

                getChildren().add(rect);

            }

        }

    }

}
