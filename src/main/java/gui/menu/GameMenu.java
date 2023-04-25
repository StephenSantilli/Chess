package gui.menu;

import game.*;
import game.LAN.ErrorMessage;
import gui.GameView;
import gui.dialog.Export;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

public class GameMenu extends Menu {

    private MenuItem newGame, undo, redo, resign, drawOffer, pause, resume, disconnect, showPgn;

    private GameView board;

    public GameMenu(GameView board) {

        super("Game");
        this.board = board;

        newGame = new MenuItem("New Game");
        newGame.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+N"));
        newGame.setOnAction(e -> {

            board.startGame();

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

            if (board.getCurrentPos() != board.getGame().getPositions().size() - 1)
                return;

            try {
                board.getGame().undo();
            } catch (Exception ex) {
            }

        });

        redo = new MenuItem("Redo");
        redo.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+Z"));
        redo.setOnAction(e -> {

            if (board.getCurrentPos() != board.getGame().getPositions().size() - 1)
                return;

            try {
                board.getGame().redo();
            } catch (Exception ex) {
            }

        });

        resign = new MenuItem("Resign");
        resign.setOnAction(e -> {

            if (board.getColor() == GameView.TWO_PLAYER) {

                board.getGame().markGameOver(
                        board.getGame().getLastPos().isWhite() ? Game.Result.BLACK_WIN : Game.Result.WHITE_WIN,
                        Game.Reason.RESIGNATION);
                return;

            }

            board.getGame().markGameOver(
                    board.getColor() == GameView.WHITE ? Game.Result.BLACK_WIN : Game.Result.WHITE_WIN,
                    Game.Reason.RESIGNATION);

        });

        drawOffer = new MenuItem("Offer Draw");
        drawOffer.setOnAction(e -> {

            if (board.getColor() == GameView.TWO_PLAYER) {

                board.getGame().markGameOver(
                        Game.Result.DRAW,
                        board.getGame().getLastPos().isWhite() ? Game.Reason.WHITE_OFFERED_DRAW
                                : Game.Reason.BLACK_OFFERED_DRAW);
                return;

            }

            try {

                board.getGame().sendDrawOffer(board.getColor() == GameView.WHITE);
            } catch (Exception ex) {

            }

        });

        disconnect = new MenuItem("Disconnect");
        disconnect.setOnAction(e -> {

            try {

                board.getClient().stop(ErrorMessage.TERMINATE);

            } catch (Exception ex) {

            }

        });

        showPgn = new MenuItem("Show PGN");
        showPgn.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+E"));
        showPgn.setOnAction(e -> {

            Export eD = new Export(board);

            eD.show();

        });

        getItems().addAll(newGame, new SeparatorMenuItem(),
                pause, resume, new SeparatorMenuItem(),
                undo, redo, new SeparatorMenuItem(),
                resign, drawOffer, new SeparatorMenuItem(),
                disconnect, new SeparatorMenuItem(),
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

            disconnect.setDisable(true);

            showPgn.setDisable(true);

        } else {

            pause.setDisable(!board.getGame().canPause());
            resume.setDisable(!board.getGame().canResume());

            undo.setDisable(!board.getGame().canUndo());
            redo.setDisable(!board.getGame().canRedo());

            disconnect.setDisable(board.getClient() == null);

            resign.setDisable(board.getGame().getResult() != Game.Result.IN_PROGRESS);
            drawOffer.setDisable(!board.getGame().canDrawOffer());

            showPgn.setDisable(false);

        }

    }

}
