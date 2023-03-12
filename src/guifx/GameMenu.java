package guifx;

import game.BoardMoveListener;
import game.Game;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

public class GameMenu extends Menu implements BoardMoveListener {

    private MenuItem undo, redo, pause, resume, export;
    private Menu gameExport;
    private Game game;

    public GameMenu(Board board, Game game) {

        super("Game");
        this.game = game;

        pause = new MenuItem("Pause");
        pause.setAccelerator(KeyCombination.keyCombination("Shortcut+P"));
        pause.setDisable(game.isPaused());
        pause.setOnAction(e -> {

            game.pauseGame();

        });

        resume = new MenuItem("Resume");
        resume.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+P"));
        resume.setDisable(!game.isPaused());
        resume.setOnAction(e -> {

            game.resumeGame();

        });

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

        export = new MenuItem("Export");
        export.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+E"));
        export.setOnAction(e -> {
            
            ExportDialog eD = new ExportDialog(board);

            eD.show();

        });


        getItems().addAll(pause, resume, new SeparatorMenuItem(), undo, redo, new SeparatorMenuItem(), export);

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
    public void posChanged(int old, int curr) {
        // TODO Auto-generated method stub
        undo.setDisable(!game.canUndo());
        redo.setDisable(!game.canRedo());
    }

    @Override
    public void redoMove() {
        // TODO Auto-generated method stub

    }

    @Override
    public void gameOver() {
        // TODO Auto-generated method stub
    }

    @Override
    public void timerChange() {
        // TODO Auto-generated method stub
    }

    private void updatePauseResume() {

        pause.setDisable(game.isPaused());
        resume.setDisable(!game.isPaused());

    }

    @Override
    public void pauseGame() {
        updatePauseResume();
    }

    @Override
    public void resumeGame() {
        updatePauseResume();
    }

}
