package gui.board;

import java.util.ArrayList;

import game.Square;
import gui.GameView;
import gui.board.element.HighlightArrow;
import javafx.scene.layout.Pane;

/**
 * A board pane that displays and manages the {@link HighlightArrow}s on the
 * board.
 * 
 * @see HighlightArrow
 */
public class Arrows extends Pane {

    /**
     * The GameView this component belongs to.
     */
    private GameView gameView;

    /**
     * The arrows the user has currently placed.
     */
    private ArrayList<HighlightArrow> highlightedArrows;

    /**
     * Creates a new arrow pane object.
     * 
     * @param gameView The GameView this arrow belongs to.
     */
    public Arrows(GameView gameView) {
        this.gameView = gameView;

        highlightedArrows = new ArrayList<>();

    }

    /**
     * Draws an arrow on the board.
     * 
     * @param start The start square to draw the arrow from.
     * @param end   The end square to draw the arrow from.
     * @param color The color of the arrow.
     */
    public void arrow(Square start, Square end, int color) {

        HighlightArrow arrow = new HighlightArrow(start, end, color, gameView);
        boolean contains = getChildren().contains(arrow);

        getChildren().removeIf(n -> {

            if (!(n instanceof HighlightArrow))
                return true;

            HighlightArrow hs = (HighlightArrow) n;

            return hs.equals(arrow);

        });

        if (contains)
            highlightedArrows.remove(arrow);
        else
            highlightedArrows.add(arrow);

    }

    public void draw() {

        highlightedArrows.clear();
        redraw();

    }

    public void redraw() {

        getChildren().clear();

        if (gameView.getGame() == null)
            return;

        for (int i = highlightedArrows.size() - 1; i >= 0; i--) {

            final HighlightArrow hs = highlightedArrows.get(i);

            getChildren().add(hs);

        }

    }

}
