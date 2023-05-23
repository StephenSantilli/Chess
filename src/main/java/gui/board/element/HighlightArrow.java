package gui.board.element;

import game.Square;
import gui.GameView;
import gui.board.Board;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

/**
 * An arrow which is displayed on the board, drawn from one square to another.
 */
public class HighlightArrow extends Pane {

    /**
     * The square the arrow should originate from.
     */
    private Square startSquare;

    /**
     * The square the arrow should end on.
     */
    private Square endSquare;

    /**
     * The color the arrow should be.
     */
    private int color;

    /**
     * Creates a new highlight arrow.
     * 
     * @param startSquare The square the arrow should originate from.
     * @param endSquare   The square the arrow should end on.
     * @param color       The color the arrow should be.
     * @param gameView    The GameView this arrow will be displayed on.
     */
    public HighlightArrow(Square startSquare, Square endSquare, int color, GameView gameView) {

        this.startSquare = startSquare;
        this.endSquare = endSquare;
        this.color = color;

        final Board board = gameView.getBoard();

        setMinSize(board.getSquareSize() * 8, board.getSquareSize() * 8);

        final double lineWidth = board.getSquareSize() / 7.0;

        final double startX = board.getXBySquare(startSquare, false) + (board.getSquareSize() / 2.0);
        final double startY = board.getYBySquare(startSquare, false) + (board.getSquareSize() / 2.0);

        final double endX = board.getXBySquare(endSquare, false) + (board.getSquareSize() / 2.0);
        final double endY = board.getYBySquare(endSquare, false) + (board.getSquareSize() / 2.0);

        // https://math.stackexchange.com/questions/9365/endpoint-of-a-line-knowing-slope-start-and-distance
        // k = +- (l) / sqrt(1 + m^2)
        // (x, y) = (endX, endY) + k(1, m)

        // ---------------(xt, yt)--------------------
        // -------------/----------\------------------
        // -----------/--------------\----------------
        // -(xh, yh)-(xe, ye)-(xea, yea)-(xha, yha)---
        // ---------|-------------------|-------------
        // ---------|-------------------|-------------
        // ---------|-------------------|-------------
        //
        // ------(xs,ys)-----------(xsa, ysa)---------

        final double negative = endX - startX;

        final double arrowLength = lineWidth * 1.75;
        final double arrowSlope = (endY - startY) / (endX - startX);

        final double headSlope = -(1 / arrowSlope);

        // bottom left
        final double ks = (negative >= 0 ? 1 : -1) * lineWidth / (Math.sqrt(1 + Math.pow(headSlope, 2)));
        final double xs = Double.isInfinite(headSlope) ? startX : ks * 1 + startX;
        final double ys = Double.isInfinite(headSlope) ? startY - lineWidth : ks * headSlope + startY;

        // bottom right
        final double xsa = startX + (startX - xs);
        final double ysa = startY - (ys - startY);

        // top left
        final double ke = (negative >= 0 ? 1 : -1) * lineWidth / (Math.sqrt(1 + Math.pow(headSlope, 2)));
        final double xe = Double.isInfinite(headSlope) ? endX : ke * 1 + endX;
        final double ye = Double.isInfinite(headSlope) ? endY - lineWidth : ke * headSlope + endY;

        // top right
        final double xea = endX + (endX - xe);
        final double yea = endY - (ye - endY);

        // arrow left
        final double kh = arrowLength / (Math.sqrt(1 + Math.pow(headSlope, 2)));
        final double xh = Double.isInfinite(headSlope) ? endX : kh * 1 + endX;
        final double yh = Double.isInfinite(headSlope) ? endY - arrowLength : kh * headSlope + endY;

        // arrow right
        final double xha = endX + (endX - xh);
        final double yha = endY - (yh - endY);

        // arrow tip
        final double kt = (negative > 0 ? 1 : -1) * arrowLength
                / (Math.sqrt(1 + Math.pow(arrowSlope, 2)));
        final double xt = kt * 1 + endX;
        final double yt = Double.isInfinite(arrowSlope)
                ? (endY) + arrowLength * (Double.compare(arrowSlope, Double.NEGATIVE_INFINITY) == 0 ? -1 : 1)
                : kt * arrowSlope + endY;

        Polygon triangle = new Polygon(
                xs, ys,
                xsa, ysa,
                xea, yea,
                xha, yha,
                xt, yt,
                xh, yh,
                xe, ye);

        triangle.setId("highlighted" + ((char) (color + 65)));

        getChildren().addAll(triangle);

    }

    /**
     * Checks if two arrows are equal to each other, including the same color.
     */
    @Override
    public boolean equals(Object o) {

        if (!(o instanceof HighlightArrow))
            return false;

        HighlightArrow hs = (HighlightArrow) o;

        return hs.getStartSquare().equals(startSquare) && hs.getEndSquare().equals(endSquare) && color == hs.getColor();

    }

    /**
     * Gets the start square of the arrow.
     * 
     * @return {@link #startSquare}
     */
    public Square getStartSquare() {
        return startSquare;
    }

    /**
     * Gets the end square of the arrow.
     * 
     * @return {@link #endSquare}
     */
    public Square getEndSquare() {
        return endSquare;
    }

    /**
     * Gets the color of the arrow.
     * 
     * @return {@link #color}
     */
    public int getColor() {
        return color;
    }

}
