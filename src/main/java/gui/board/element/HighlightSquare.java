package gui.board.element;

import game.Square;
import gui.GameView;
import gui.board.Board;
import javafx.scene.layout.Pane;

/**
 * A highlight which is placed over a square on the board.
 */
public class HighlightSquare extends Pane {

    /**
     * The square being highlighted.
     */
    private Square square;

    /**
     * The color of the highlight.
     */
    private int color;

    /**
     * Creates a new highlighted square.
     * 
     * @param square   The square to highlight.
     * @param color    The color of the highlight.
     * @param gameView The GameView that this square is a part of.
     */
    public HighlightSquare(Square square, int color, GameView gameView) {

        this.square = square;
        this.color = color;

        final Board board = gameView.getBoard();

        setId("highlighted" + ((char) (color + 65)));

        setMinSize(board.getSquareSize(), board.getSquareSize());
        setPrefSize(board.getSquareSize(), board.getSquareSize());

        setLayoutX(board.getXBySquare(square));
        setLayoutY(board.getYBySquare(square));

        setStyle("-fx-background-radius: " + Board.getSquareCornerRadius(square, gameView.isFlipped()));

    }

    /**
     * Gets the square that is highlighted.
     * 
     * @return {@link #square}
     */
    public Square getSquare() {
        return square;
    }

    /**
     * Gets the color of the highlight.
     * 
     * @return {@link #color}
     */
    public int getColor() {
        return color;
    }

    /**
     * Checks if two highlights are the same, including by color.
     */
    @Override
    public boolean equals(Object o) {

        if (!(o instanceof HighlightSquare))
            return false;

        HighlightSquare hs = (HighlightSquare) o;

        return hs.getSquare().equals(square) && color == hs.getColor();

    }

}
