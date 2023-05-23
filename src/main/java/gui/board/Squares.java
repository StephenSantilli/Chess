package gui.board;

import game.Square;
import gui.GameView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * The pane that displays the underlying squares of the board.
 */
public class Squares extends Pane {

    /**
     * The GameView that contains this pane.
     */
    private GameView gameView;

    /**
     * Creates a new squares pane.
     * 
     * @param gameView The GameView that contains this pane.
     */
    public Squares(GameView gameView) {
        this.gameView = gameView;
    }

    /**
     * Clears any previously drawn squares, then draws the new squares based on the
     * current {@link Board#squareSize}.
     */
    public void draw() {

        getChildren().clear();

        final Board board = gameView.getBoard();
        final double squareSize = board.getSquareSize();

        for (int r = 8; r > 0; r--) {

            for (int f = 8; f > 0; f--) {

                final Square square = new Square(f, r);
                final String radius = Board.getSquareCornerRadius(square, gameView.isFlipped());

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
