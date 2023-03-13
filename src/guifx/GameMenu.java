package guifx;

import game.BoardMoveListener;
import game.Game;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

public class GameMenu extends Menu implements BoardMoveListener {

    private MenuItem newGame, undo, redo, pause, resume, export;
    private Menu gameExport;
    private Board board;

    public GameMenu(Board board) {

        super("Game");
        this.board = board;

        MenuItem newGame = new MenuItem("New Game");
        newGame.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+N"));
        newGame.setOnAction(e -> {

            board.newGame();

        });

        pause = new MenuItem("Pause");
        pause.setAccelerator(KeyCombination.keyCombination("Shortcut+P"));
        pause.setDisable(board.getGame().isPaused());
        pause.setOnAction(e -> {

            board.getGame().pauseGame();

        });

        resume = new MenuItem("Resume");
        resume.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+P"));
        resume.setDisable(!board.getGame().isPaused());
        resume.setOnAction(e -> {

            board.getGame().resumeGame();

        });

        undo = new MenuItem("Undo");
        undo.setAccelerator(KeyCombination.keyCombination("Shortcut+Z"));
        undo.setDisable(!board.getGame().canUndo());
        undo.setOnAction(e -> {

            board.getGame().undoMove();

        });

        redo = new MenuItem("Redo");
        redo.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+Z"));
        redo.setDisable(!board.getGame().canRedo());
        redo.setOnAction(e -> {
            board.getGame().redoMove();
        });

        export = new MenuItem("Export");
        export.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+E"));
        export.setOnAction(e -> {

            ExportDialog eD = new ExportDialog(board);

            eD.show();

        });

        getItems().addAll(newGame, new SeparatorMenuItem(), pause, resume, new SeparatorMenuItem(), undo, redo,
                new SeparatorMenuItem(), export);

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
        undo.setDisable(!board.getGame().canUndo());
        redo.setDisable(!board.getGame().canRedo());
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

    public void updatePauseResume() {

        pause.setDisable(board.getGame().isPaused());
        resume.setDisable(!board.getGame().isPaused());

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
