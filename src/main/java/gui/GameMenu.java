package gui;

import game.Game;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

public class GameMenu extends Menu {

    private MenuItem newGame, undo, redo, resign, drawOffer, pause, resume, showPgn;

    private Board board;

    public GameMenu(Board board) {

        super("Game");
        this.board = board;

        newGame = new MenuItem("New Game");
        newGame.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+N"));
        newGame.setOnAction(e -> {

            board.startGame(null);

        });

        pause = new MenuItem("Pause");
        pause.setAccelerator(KeyCombination.keyCombination("Shortcut+P"));
        pause.setOnAction(e -> {

            try {
                board.getGame().pause();
            } catch (Exception ex) {
            }

        });

        resume = new MenuItem("Resume");
        resume.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+P"));
        resume.setOnAction(e -> {

            try {
                board.getGame().resume();
            } catch (Exception ex) {
            }

        });

        undo = new MenuItem("Undo");
        undo.setAccelerator(KeyCombination.keyCombination("Shortcut+Z"));
        undo.setOnAction(e -> {

            try {
                board.getGame().undo();
            } catch (Exception ex) {
            }

        });

        redo = new MenuItem("Redo");
        redo.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+Z"));
        redo.setOnAction(e -> {

            try {
                board.getGame().redo();
            } catch (Exception ex) {
            }

        });

        resign = new MenuItem("Resign");
        resign.setOnAction(e -> {

            if (board.getColor() == Board.TWO_PLAYER) {

                board.getGame().markGameOver(
                        board.getGame().isWhiteTurn() ? Game.RESULT_BLACK_WIN : Game.RESULT_WHITE_WIN,
                        Game.REASON_RESIGNATION);
                return;

            }

            board.getGame().markGameOver(
                    board.getColor() == Board.WHITE ? Game.RESULT_BLACK_WIN : Game.RESULT_WHITE_WIN,
                    Game.REASON_RESIGNATION);

        });

        drawOffer = new MenuItem("Offer Draw");
        drawOffer.setOnAction(e -> {

            if (board.getColor() == Board.TWO_PLAYER) {

                board.getGame().markGameOver(
                        Game.RESULT_DRAW,
                        board.getGame().isWhiteTurn() ? Game.REASON_WHITE_OFFERED_DRAW
                                : Game.REASON_BLACK_OFFERED_DRAW);
                return;

            }

            try {

                board.getGame().sendDrawOffer(board.getColor() == Board.WHITE);
            } catch (Exception ex) {

            }

        });

        showPgn = new MenuItem("Show PGN");
        showPgn.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+E"));
        showPgn.setOnAction(e -> {

            ExportDialog eD = new ExportDialog(board);

            eD.show();

        });

        getItems().addAll(newGame, new SeparatorMenuItem(),
                pause, resume, new SeparatorMenuItem(),
                undo, redo, new SeparatorMenuItem(),
                resign, drawOffer, new SeparatorMenuItem(),
                showPgn);

    }

    public void update() {

        if (board.getGame() == null) {

            pause.setDisable(true);
            resume.setDisable(true);

            undo.setDisable(true);
            redo.setDisable(true);

            resign.setDisable(true);
            drawOffer.setDisable(true);

            showPgn.setDisable(true);

        } else {

            pause.setDisable(!board.getGame().canPause());
            resume.setDisable(!board.getGame().canResume());

            undo.setDisable(!board.getGame().canUndo());
            redo.setDisable(!board.getGame().canRedo());

            resign.setDisable(board.getGame().getResult() != Game.RESULT_IN_PROGRESS);
            drawOffer.setDisable(!board.getGame().canDrawOffer());

            showPgn.setDisable(false);

        }

    }

}
