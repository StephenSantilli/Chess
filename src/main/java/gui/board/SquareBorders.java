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

        getChildren().clear();

        final Board board = gameView.getBoard();
        final double squareSize = board.getSquareSize();

        if (square == null || !square.isValid())
            return;

        final double strokeWidth = squareSize / 20.0;

        final double x = board.getXBySquare(square);
        final double y = board.getYBySquare(square);

        Pane border = new Pane();
        border.setId("squareBorder");

        border.setMinSize(squareSize, squareSize);

        border.setLayoutX(x);
        border.setLayoutY(y);

        border.setStyle(
                "-fx-border-width: " + strokeWidth + ";" +
                        "-fx-border-radius: " + Board.getSquareCornerRadius(square) + ";");

        getChildren().add(border);

    }

}
