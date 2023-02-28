package guifx;

import game.Position;
import game.Game;
import game.Move;

import javafx.scene.layout.VBox;

public class MovePane extends VBox {

    Game g;

    public MovePane(Game g) {

        this.g = g;

    }

    public void updateMovePane() {

        getChildren().clear();

        for (int i = 1; i < g.getPositions().size(); i += 2) {

            Position p1 = g.getPositions().get(i);

            Position p2 = null;
            if (g.getPositions().size() > (i + 1))
                p2 = g.getPositions().get(i + 1);

            getChildren().add(new MoveRow((i / 2) + 1, p1.getMove(), p2 == null ? null : p2.getMove()));

        }

    }

}
