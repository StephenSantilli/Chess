package gui.board;

import java.util.ArrayList;

import game.Position;
import game.Square;
import gui.GameView;
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
    private ArrayList<HighlightSquare> highlighted;

    public ArrayList<HighlightSquare> getHighlighted() {
        return highlighted;
    }

    public Highlights(GameView gameView) {
        this.gameView = gameView;
        highlighted = new ArrayList<>();

    }

    public void highlight(Square square, int color) {

        HighlightSquare rect = new HighlightSquare(square, color, gameView);
        boolean contains = getChildren().contains(rect);

        getChildren().removeIf(n -> {

            if (!(n instanceof HighlightSquare))
                return false;

            HighlightSquare hs = (HighlightSquare) n;

            return hs.getSquare().equals(square);

        });

        if (contains) {
            highlighted.remove(rect);
            return;
        }

        highlighted.add(rect);

    }

    public void draw() {

        getChildren().clear();
        highlighted.clear();

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

    public void redraw() {

        getChildren().clear();

        ArrayList<Square> drawn = new ArrayList<>();

        for (int i = highlighted.size() - 1; i >= 0; i--) {

            final HighlightSquare hs = highlighted.get(i);

            if (!drawn.contains(hs.getSquare())) {
                getChildren().add(hs);
                drawn.add(hs.getSquare());
            }

        }

    }

}
