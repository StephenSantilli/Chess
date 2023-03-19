package gui;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

public class GameMenu extends Menu {

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
        pause.setOnAction(e -> {

            try {
                board.getActivePlayer().pauseGame();
            } catch (Exception ex) {
            }

        });

        resume = new MenuItem("Resume");
        resume.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+P"));
        resume.setOnAction(e -> {

            try {
                board.getActivePlayer().resumeGame();
            } catch (Exception ex) {
            }

        });

        undo = new MenuItem("Undo");
        undo.setAccelerator(KeyCombination.keyCombination("Shortcut+Z"));
        undo.setOnAction(e -> {

            try {
                board.getActivePlayer().undo();
            } catch (Exception ex) {
            }

        });

        redo = new MenuItem("Redo");
        redo.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+Z"));
        redo.setOnAction(e -> {

            try {
                board.getActivePlayer().redo();
            } catch (Exception ex) {
            }

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

    public void update() {
        
        if (board.getGame() == null) {

            pause.setDisable(true);
            resume.setDisable(true);

            undo.setDisable(true);
            redo.setDisable(true);

            export.setDisable(true);

        } else {

            pause.setDisable(!board.getActivePlayer().canPause());
            resume.setDisable(!board.getActivePlayer().canResume());

            undo.setDisable(!board.getActivePlayer().canUndo());
            redo.setDisable(!board.getActivePlayer().canRedo());

            export.setDisable(false);

        }

    }

}
