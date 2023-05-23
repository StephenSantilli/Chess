package gui.menu;

import gui.GameView;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCombination;

/**
 * The menu bar tab that contains view options.
 */
public class ViewMenu extends Menu {

    /**
     * The menu item which allows the user to control whether or not the board is
     * flipped.
     * 
     * @see GameView#isFlipped()
     */
    private CheckMenuItem flip;

    /**
     * The menu item which allows the user to control whether or not the board
     * should automatically flip after each move. Only enabled for two player games.
     * 
     * @see GameView#isAutoFlip()
     */
    private CheckMenuItem autoFlip;

    /**
     * The parent {@link GameView} that manages this menu.
     */
    private GameView gameView;

    /**
     * Creates a new "View" menu tab.
     * 
     * @param gameView The {@link GameView} that manages this menu tab.
     */
    public ViewMenu(GameView gameView) {

        super("View");
        this.gameView = gameView;

        flip = new CheckMenuItem("Flip");
        flip.setAccelerator(KeyCombination.keyCombination("Shortcut+F"));
        flip.setOnAction(e -> gameView.flip());

        autoFlip = new CheckMenuItem("Auto Flip");
        autoFlip.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+F"));
        autoFlip.setOnAction(e -> gameView.setAutoFlip(autoFlip.isSelected()));

        update();

        getItems().addAll(flip, autoFlip);

    }

    /**
     * Updates the menu based on the current conditions.
     */
    public void update() {

        if (gameView.getGame() == null) {

            flip.setDisable(true);
            flip.setSelected(false);

            autoFlip.setSelected(false);
            autoFlip.setDisable(true);

        } else {

            flip.setDisable(false);

            if (gameView.getColor().equals(GameView.Color.WHITE)) {

                flip.setSelected(gameView.isFlipped());
                autoFlip.setDisable(true);

            } else if (gameView.getColor().equals(GameView.Color.BLACK)) {

                flip.setSelected(!gameView.isFlipped());
                autoFlip.setDisable(true);

            } else if (gameView.getColor().equals(GameView.Color.TWO_PLAYER)) {

                flip.setSelected(gameView.isFlipped() == gameView.getGame().getLastPos().isWhite());
                autoFlip.setDisable(false);

            }

        }

    }

}
