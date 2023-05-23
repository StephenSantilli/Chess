package gui.menu;

import game.*;
import game.LAN.ErrorMessage;
import gui.GameView;
import gui.dialog.Export;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

/**
 * The menu bar tab that contains game options.
 */
public class GameMenu extends Menu {

    /**
     * The menu item which allows the user to open the new game dialog.
     */
    private MenuItem newGame;

    /**
     * The menu item which allows the user to undo the last move.
     * 
     * @see Game#undo()
     */
    private MenuItem undo;

    /**
     * The menu item which allows the user to redo the move that was previously
     * undone.
     * 
     * @see Game#redo()
     */
    private MenuItem redo;

    /**
     * The menu item which allows the user to resign.
     */
    private MenuItem resign;

    /**
     * The menu item which allows the user to send a draw offer to the opponent.
     */
    private MenuItem drawOffer;

    /**
     * The menu item which allows the user to pause the game.
     */
    private MenuItem pause;

    /**
     * The menu item which allows the user to resume the game.
     */
    private MenuItem resume;

    /**
     * The menu item which allows the user to disconnect from their LAN opponent.
     */
    private MenuItem disconnect;

    /**
     * The menu item which allows the user to export the game by displaying the
     * export dialog.
     */
    private MenuItem export;

    /**
     * The parent {@link GameView} that manages this menu.
     */
    private GameView gameView;

    /**
     * Creates a new "Game" menu tab.
     * 
     * @param gameView The {@link GameView} that manages this menu tab.
     */
    public GameMenu(GameView gameView) {

        super("Game");
        this.gameView = gameView;

        newGame = new MenuItem("New Game");
        newGame.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+N"));
        newGame.setOnAction(e -> {

            gameView.startGame();

        });

        pause = new MenuItem("Pause");
        pause.setAccelerator(KeyCombination.keyCombination("Shortcut+P"));
        pause.setOnAction(e -> {

            try {
                gameView.getGame().pause();
            } catch (Exception ex) {
            }

        });

        resume = new MenuItem("Resume");
        resume.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+P"));
        resume.setOnAction(e -> {

            try {
                gameView.getGame().resume();
            } catch (Exception ex) {
            }

        });

        undo = new MenuItem("Undo");
        undo.setAccelerator(KeyCombination.keyCombination("Shortcut+Z"));
        undo.setOnAction(e -> {

            if (gameView.getCurrentPos() != gameView.getGame().getPositions().size() - 1)
                return;

            try {
                gameView.getGame().undo();
            } catch (Exception ex) {
            }

        });

        redo = new MenuItem("Redo");
        redo.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+Z"));
        redo.setOnAction(e -> {

            if (gameView.getCurrentPos() != gameView.getGame().getPositions().size() - 1)
                return;

            try {
                gameView.getGame().redo();
            } catch (Exception ex) {
            }

        });

        resign = new MenuItem("Resign");
        resign.setOnAction(e -> {

            if (gameView.getColor().equals(GameView.Color.TWO_PLAYER)) {

                gameView.getGame().markGameOver(
                        gameView.getGame().getLastPos().isWhite() ? Game.Result.BLACK_WIN : Game.Result.WHITE_WIN,
                        Game.Reason.RESIGNATION);
                return;

            }

            gameView.getGame().markGameOver(
                    gameView.getColor().equals(GameView.Color.WHITE) ? Game.Result.BLACK_WIN : Game.Result.WHITE_WIN,
                    Game.Reason.RESIGNATION);

        });

        drawOffer = new MenuItem("Offer Draw");
        drawOffer.setOnAction(e -> {

            if (gameView.getColor().equals(GameView.Color.TWO_PLAYER)) {

                gameView.getGame().markGameOver(
                        Game.Result.DRAW,
                        gameView.getGame().getLastPos().isWhite() ? Game.Reason.WHITE_OFFERED_DRAW
                                : Game.Reason.BLACK_OFFERED_DRAW);
                return;

            }

            try {

                gameView.getGame().sendDrawOffer(gameView.getColor().equals(GameView.Color.WHITE));
            } catch (Exception ex) {

            }

        });

        disconnect = new MenuItem("Disconnect");
        disconnect.setOnAction(e -> {

            try {

                gameView.getClient().stop(ErrorMessage.TERMINATE);

            } catch (Exception ex) {

            }

        });

        export = new MenuItem("Export");
        export.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+E"));
        export.setOnAction(e -> {

            Export eD = new Export(gameView);

            eD.show();

        });

        getItems().addAll(newGame, new SeparatorMenuItem(),
                pause, resume, new SeparatorMenuItem(),
                undo, redo, new SeparatorMenuItem(),
                resign, drawOffer, new SeparatorMenuItem(),
                disconnect, new SeparatorMenuItem(),
                export);

    }

    /**
     * Updates the menu based on the current conditions.
     */
    public void update() {

        if (gameView.getGame() == null) {

            pause.setDisable(true);
            resume.setDisable(true);

            undo.setDisable(true);
            redo.setDisable(true);

            resign.setDisable(true);
            drawOffer.setDisable(true);

            disconnect.setDisable(true);

            export.setDisable(true);

        } else {

            pause.setDisable(!gameView.getGame().canPause());
            resume.setDisable(!gameView.getGame().canResume());

            undo.setDisable(!gameView.getGame().canUndo());
            redo.setDisable(!gameView.getGame().canRedo());

            disconnect.setDisable(gameView.getClient() == null);

            resign.setDisable(gameView.getGame().getResult() != Game.Result.IN_PROGRESS);
            drawOffer.setDisable(!gameView.getGame().canDrawOffer());

            export.setDisable(false);

        }

    }

}
