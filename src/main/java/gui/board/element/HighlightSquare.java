package gui.board.element;

import game.Square;
import gui.GameView;
import gui.board.Board;
import javafx.scene.layout.Pane;

public class HighlightSquare extends Pane {

    private Square square;
    private int color;

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

    public Square getSquare() {
        return square;
    }

    public void setSquare(Square square) {
        this.square = square;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof HighlightSquare))
            return false;

        HighlightSquare hs = (HighlightSquare) o;

        return hs.getSquare().equals(square) && color == hs.getColor();

    }

}
