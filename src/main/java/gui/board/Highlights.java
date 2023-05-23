package gui.board;

import java.util.ArrayList;

import game.Position;
import game.Square;
import gui.GameView;
import gui.board.element.HighlightSquare;
import javafx.scene.layout.Pane;

/**
 * A board component that draws the "highlights" over squares.
 * 
 * <p>
 * Will highlight the origin and destination squares of the previous move as
 * well as the square of the currently active piece.
 */
public class Highlights extends Pane {

    /**
     * The GameView this component belongs to.
     */
    private GameView gameView;

    /**
     * The squares the user has highlighted, not the active, origin, or
     * destination squares.
     */
    private ArrayList<HighlightSquare> highlightedSquares;

    /**
     * Creates a new highlights pane.
     * 
     * @param gameView The GameView which contains this pane.
     */
    public Highlights(GameView gameView) {
        this.gameView = gameView;
        highlightedSquares = new ArrayList<>();

    }

    /**
     * Gets the squares that are currently highlighted.
     * 
     * @return {@link #highlightedSquares}
     */
    public ArrayList<HighlightSquare> getHighlightedSquares() {
        return highlightedSquares;
    }

    /**
     * Highlights a square.
     * 
     * @param square The square to highlight.
     * @param color  The color of the highlight.
     */
    public void highlight(Square square, int color) {

        HighlightSquare rect = new HighlightSquare(square, color, gameView);
        boolean contains = getChildren().contains(rect);

        getChildren().removeIf(n -> {

            if (!(n instanceof HighlightSquare))
                return true;

            HighlightSquare hs = (HighlightSquare) n;

            return hs.equals(rect);

        });

        if (contains)
            highlightedSquares.remove(rect);
        else
            highlightedSquares.add(rect);

    }

    /**
     * Clears the user-added highlights and redraws the default highlights.
     */
    public void draw() {

        getChildren().clear();
        highlightedSquares.clear();

        final Board board = gameView.getBoard();

        if (gameView.getGame() == null)
            return;

        if (gameView.getCurrentPos() > 0) {

            final Position pos = gameView.getGame().getPositions().get(gameView.getCurrentPos());

            final Square origin = pos.getMove().getOrigin();

            highlight(origin, 0);

            final Square destination = pos.getMove().getDestination();

            highlight(destination, 0);

        }

        if (board.getDragging() != null || board.getActive() != null) {

            final Square aSquare = board.getDragging() != null ? board.getDragging().getPiece().getSquare()
                    : board.getActive().getPiece().getSquare();

            highlight(aSquare, 1);

        }

        redraw();

    }

    /**
     * Redraws the highlights.
     */
    public void redraw() {

        getChildren().clear();

        ArrayList<Square> drawn = new ArrayList<>();

        for (int i = highlightedSquares.size() - 1; i >= 0; i--) {

            final HighlightSquare hs = highlightedSquares.get(i);

            if (!drawn.contains(hs.getSquare())) {
                getChildren().add(hs);
                drawn.add(hs.getSquare());
            }

        }

    }

}
