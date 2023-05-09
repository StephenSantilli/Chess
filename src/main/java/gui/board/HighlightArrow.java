package gui.board;

import game.Square;
import gui.GameView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
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

        Line rect = new Line(startX, startY, endX, endY);
        rect.setStrokeWidth(lineWidth);

        final double negative = endX - startX;

        final double l = lineWidth * 1.75;
        final double mr = (endY - startY) / (endX - startX);

        final double m = -(1 / mr);

        // Calculating arrow-head line end point
        // https://math.stackexchange.com/questions/9365/endpoint-of-a-line-knowing-slope-start-and-distance
        // k = +- (l) / sqrt(1 + m^2)
        // (x, y) = (endX, endY) + k(1, m)
        final double k = l / (Math.sqrt(1 + Math.pow(m, 2)));
        final double x = Double.isInfinite(m) ? endX : k * 1 + endX;
        final double y = Double.isInfinite(m) ? endY - l : k * m + endY;

        // Calculating arrow head tip
        final double kt = (negative > 0 ? 1 : -1) * l
                / (Math.sqrt(1 + Math.pow(mr, 2)));
        final double xt = kt * 1 + endX;
        final double yt = Double.isInfinite(mr)
                ? (endY) + l * (Double.compare(mr, Double.NEGATIVE_INFINITY) == 0 ? -1 : 1)
                : kt * mr + endY;

        Polygon triangle = new Polygon(
                x, y,
                endX + (endX - x), endY - (y - endY),
                xt, yt);

        rect.setId("highlighted" + ((char) (color + 65)));
        triangle.setId("highlighted" + ((char) (color + 65)));

        getChildren().addAll(rect, triangle);

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
