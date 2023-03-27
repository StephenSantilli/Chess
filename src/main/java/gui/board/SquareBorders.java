package gui.board;

import game.Square;
import gui.GameView;
import javafx.scene.layout.Pane;

public class SquareBorders extends Pane {

    private GameView gameView;

    public SquareBorders(GameView gameView) {
        this.gameView = gameView;
    }

    public void drawBorder(Square square) {

        final Board board = gameView.getBoard();

        getChildren().clear();

        final double squareSize = board.getSquareSize();

        if (square == null || !square.isValid())
            return;

        double strokeWidth = squareSize / 20.0;

        double x = board.getXBySquare(square);
        double y = board.getYBySquare(square);

        Pane border = new Pane();
        border.setId("squareBorder");

        border.setMinSize(squareSize, squareSize);

        border.setLayoutX(x);
        border.setLayoutY(y);

        border.setStyle(
                "-fx-border-width: " + strokeWidth + "; -fx-border-radius: " + Board.getSquareCornerRadius(square) + ";");

        getChildren().add(border);

    }

}
