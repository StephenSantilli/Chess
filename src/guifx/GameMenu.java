package guifx;

import game.BoardMoveListener;
import game.Game;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;

public class GameMenu extends Menu implements BoardMoveListener {
    
    private MenuItem undo, redo, pgnImport, pgnExport;
    private Game game;

    public GameMenu(Game game) {

        super("Game");
        this.game = game;

        undo = new MenuItem("Undo");
        undo.setAccelerator(KeyCombination.keyCombination("Shortcut+Z"));
        undo.setDisable(!game.canUndo());
        undo.setOnAction(e -> {

            game.undoMove();

        });

        redo = new MenuItem("Redo");
        redo.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+Z"));
        redo.setDisable(!game.canRedo());
        redo.setOnAction(e -> {
            game.redoMove();
        });

        getItems().addAll(undo, redo);

    }

    @Override
    public void moveMade() {
        // TODO Auto-generated method stub

    }

    @Override
    public void undoMove() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetMoves() {
        // TODO Auto-generated method stub

    }

    @Override
    public void posChanged() {
        // TODO Auto-generated method stub
        undo.setDisable(!game.canUndo());
        redo.setDisable(!game.canRedo());
    }

    @Override
    public void redoMove() {
        // TODO Auto-generated method stub

    }

}
