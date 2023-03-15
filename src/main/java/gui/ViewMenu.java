package gui;


import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCombination;

public class ViewMenu extends Menu {

    private CheckMenuItem flip;
    private Board board;

    public ViewMenu(Board board) {

        super("View");
        this.board = board;

        flip = new CheckMenuItem("Flip");
        flip.setAccelerator(KeyCombination.keyCombination("Shortcut+F"));
        flip.setOnAction(e -> {

            board.flipBoard();

        });

        getItems().addAll(flip);

    }

    public void update() {

        flip.setSelected(board.isWhite() ? board.isFlipped() : !board.isFlipped());

    }

}
