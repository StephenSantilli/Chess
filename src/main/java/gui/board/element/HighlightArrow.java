package gui.board.element;

import game.Square;
import gui.GameView;
import gui.board.Board;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

public class HighlightArrow extends Pane {

    private Square startSquare;
    private Square endSquare;
    private int color;

    public HighlightArrow(Square startSquare, Square endSquare, int color, GameView gameView) {

        this.startSquare = startSquare;
        this.endSquare = endSquare;
        this.color = color;

        final Board board = gameView.getBoard();
        setMinSize(board.getSquareSize() * 8, board.getSquareSize() * 8);

        final double lineWidth = board.getSquareSize() / 6.0;

        final double startX = board.getXBySquare(startSquare, false) + (board.getSquareSize() / 2.0);
        final double startY = board.getYBySquare(startSquare, false) + (board.getSquareSize() / 2.0);
        final double endX = board.getXBySquare(endSquare, false) + (board.getSquareSize() / 2.0);
        final double endY = board.getYBySquare(endSquare, false) + (board.getSquareSize() / 2.0);

        // https://math.stackexchange.com/questions/9365/endpoint-of-a-line-knowing-slope-start-and-distance
        // k = +- (l) / sqrt(1 + m^2)
        // (x, y) = (endX, endY) + k(1, m)

        final double negative = endX - startX;

        final double arrowLength = lineWidth * 1.75;
        final double arrowSlope = (endY - startY) / (endX - startX);

        final double headSlope = -(1 / arrowSlope);

        final double ks = (negative >= 0 ? 1 : -1) * lineWidth / (Math.sqrt(1 + Math.pow(headSlope, 2)));
        final double xs = Double.isInfinite(headSlope) ? startX : ks * 1 + startX;
        final double ys = Double.isInfinite(headSlope) ? startY - lineWidth : ks * headSlope + startY;

        final double ke = (negative >= 0 ? 1 : -1) * lineWidth / (Math.sqrt(1 + Math.pow(headSlope, 2)));
        final double xe = Double.isInfinite(headSlope) ? endX : ke * 1 + endX;
        final double ye = Double.isInfinite(headSlope) ? endY - lineWidth : ke * headSlope + endY;

        // Calculating arrow-head line end point

        final double k = arrowLength / (Math.sqrt(1 + Math.pow(headSlope, 2)));
        final double x = Double.isInfinite(headSlope) ? endX : k * 1 + endX;
        final double y = Double.isInfinite(headSlope) ? endY - arrowLength : k * headSlope + endY;

        // Calculating arrow head tip
        final double kt = (negative > 0 ? 1 : -1) * arrowLength
                / (Math.sqrt(1 + Math.pow(arrowSlope, 2)));
        final double xt = kt * 1 + endX;
        final double yt = Double.isInfinite(arrowSlope)
                ? (endY) + arrowLength * (Double.compare(arrowSlope, Double.NEGATIVE_INFINITY) == 0 ? -1 : 1)
                : kt * arrowSlope + endY;

        Polygon triangle = new Polygon(
                xs, ys,
                startX + (startX - xs), startY - (ys - startY),
                endX + (endX - xe), endY - (ye - endY),
                endX + (endX - x), endY - (y - endY),
                xt, yt,
                x, y,
                xe, ye);

        triangle.setId("highlighted" + ((char) (color + 65)));

        getChildren().addAll(triangle);

    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof HighlightArrow))
            return false;

        HighlightArrow hs = (HighlightArrow) o;

        return hs.getStartSquare().equals(startSquare) && hs.getEndSquare().equals(endSquare) && color == hs.getColor();

    }

    public Square getStartSquare() {
        return startSquare;
    }

    public void setStartSquare(Square startSquare) {
        this.startSquare = startSquare;
    }

    public Square getEndSquare() {
        return endSquare;
    }

    public void setEndSquare(Square endSquare) {
        this.endSquare = endSquare;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
