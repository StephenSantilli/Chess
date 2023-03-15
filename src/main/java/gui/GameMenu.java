package gui;

import game.BoardMoveListener;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

public class GameMenu extends Menu implements BoardMoveListener {

    private MenuItem newGame, undo, redo, pause, resume, export;

    private Board board;

    public GameMenu(Board board) {

        super("Game");
        this.board = board;

        newGame = new MenuItem("New Game");
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

        export = new MenuItem("Show PGN");
        export.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+E"));
        export.setOnAction(e -> {

            ExportDialog eD = new ExportDialog(board);

            eD.show();

        });

        getItems().addAll(newGame, new SeparatorMenuItem(),
                pause, resume, new SeparatorMenuItem(),
                undo, redo, new SeparatorMenuItem(),
                export);

    }

    @Override
    public void moveMade() {

    }

    @Override
    public void undoMove() {

    }

    @Override
    public void resetMoves() {

    }

    @Override
    public void posChanged(int old, int curr) {
        undo.setDisable(!board.getGame().canUndo());
        redo.setDisable(!board.getGame().canRedo());
    }

    @Override
    public void redoMove() {

    }

    @Override
    public void gameOver() {

    }

    @Override
    public void timerChange() {

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
