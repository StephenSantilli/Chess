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

        update();

        getItems().addAll(flip);

    }

    public void update() {

        if (board.getGame() == null) {

            flip.setDisable(true);
            flip.setSelected(false);

        } else {

            flip.setDisable(false);
            if(board.getColor() == Board.WHITE) {
                flip.setSelected(board.isFlipped());
            } else if(board.getColor() == Board.BLACK) {
                flip.setSelected(!board.isFlipped());
            } else if(board.getColor() == Board.TWO_PLAYER) {
                flip.setSelected(!board.isFlipped() == board.getGame().isCountdownWhite());
            }

        }

    }

}
