package guifx;

import game.Game;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.stage.Window;

public class ViewMenu extends Menu {

    private CheckMenuItem flip;
    private Game game;
    private Board board;

    public ViewMenu(Board board, Game game) {

        super("View");
        this.game = game;
        this.board = board;

        flip = new CheckMenuItem("Flip");
        flip.setAccelerator(KeyCombination.keyCombination("F"));
        flip.setOnAction(e -> {

            board.flipBoard();

        });

        getItems().addAll(flip);

    }

    public void boardUpdated() {

        flip.setSelected(board.isWhite() ? board.isFlipped() : !board.isFlipped());

    }

}
