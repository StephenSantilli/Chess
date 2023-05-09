package gui.board;

import java.util.ArrayList;

import game.Position;
import game.Square;
import gui.GameView;
import javafx.scene.layout.Pane;

/**
 * A board component that draws the arrows on the board.
 */
public class Arrows extends Pane {

    /**
     * The GameView this component belongs to.
     */
    private GameView gameView;

    /**
     * The squares the user has highlighted, not the active, origin, or
     * destination squares.
     */
    private ArrayList<HighlightArrow> highlightedArrows;

    public Arrows(GameView gameView) {
        this.gameView = gameView;

        highlightedArrows = new ArrayList<>();

    }

    public void arrow(Square start, Square end, int color) {

        HighlightArrow arrow = new HighlightArrow(start, end, color, gameView);
        boolean contains = getChildren().contains(arrow);

        getChildren().removeIf(n -> {

            if (!(n instanceof HighlightArrow))
                return true;

            HighlightArrow hs = (HighlightArrow) n;

            return hs.equals(arrow);

        });

        if (contains) {
            highlightedArrows.remove(arrow);
            return;
        }

        highlightedArrows.add(arrow);

    }

    public void draw() {

        getChildren().clear();

        highlightedArrows.clear();

        final Board board = gameView.getBoard();

        if (gameView.getGame() == null)
            return;

        redraw();

    }

    public void redraw() {

        getChildren().clear();

        for (int i = highlightedArrows.size() - 1; i >= 0; i--) {

            final HighlightArrow hs = highlightedArrows.get(i);

            getChildren().add(hs);

        }

    }

}
