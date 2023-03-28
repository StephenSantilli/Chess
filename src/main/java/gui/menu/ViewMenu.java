package gui.menu;

import gui.GameView;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCombination;

public class ViewMenu extends Menu {

    private CheckMenuItem flip;
    private CheckMenuItem autoFlip;
    private GameView board;

    public ViewMenu(GameView board) {

        super("View");
        this.board = board;

        flip = new CheckMenuItem("Flip");
        flip.setAccelerator(KeyCombination.keyCombination("Shortcut+F"));
        flip.setOnAction(e -> {

            board.flip();

        });

        autoFlip = new CheckMenuItem("Auto Flip");
        autoFlip.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+F"));
        autoFlip.setOnAction(e -> {

            board.setAutoFlip(autoFlip.isSelected());

        });

        update();

        getItems().addAll(flip, autoFlip);

    }

    public void update() {

        if (board.getGame() == null) {

            flip.setDisable(true);
            flip.setSelected(false);

            autoFlip.setSelected(false);
            autoFlip.setDisable(true);

        } else {

            flip.setDisable(false);

            if (board.getColor() == GameView.WHITE) {
                flip.setSelected(board.isFlipped());
                autoFlip.setDisable(true);
            } else if (board.getColor() == GameView.BLACK) {
                flip.setSelected(!board.isFlipped());
                autoFlip.setDisable(true);
            } else if (board.getColor() == GameView.TWO_PLAYER) {
                flip.setSelected(board.isFlipped() == board.getGame().getLastPos().isWhite());
                autoFlip.setDisable(false);
            }

        }

    }

}
