package gui.board;

import game.Square;

import gui.GameView;

import javafx.scene.layout.Pane;

/**
 * Displays borders around the squares when a piece is being dragged.
 */
public class SquareBorders extends Pane {

    /**
     * The GameView that contains this pane.
     */
    private GameView gameView;

    /**
     * Creates a new square borders pane.
     * 
     * @param gameView The GameView that contains this pane.
     */
    public SquareBorders(GameView gameView) {
        this.gameView = gameView;
    }

    /**
     * Clears any previous borders and draws a new border around the given square.
     * 
     * @param square The square to draw a border around.
     */
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
                        "-fx-border-radius: " + Board.getSquareCornerRadius(square, gameView.isFlipped()) + ";");

        getChildren().add(border);

    }

}
