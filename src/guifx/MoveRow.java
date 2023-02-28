package guifx;

import game.Move;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class MoveRow extends HBox {

    public MoveRow(int moveNumber, Move m1, Move m2) {

        Label l = new Label(moveNumber + ".");
        getChildren().add(l);
        setHgrow(l, Priority.NEVER);

        Button btn1 = new Button(m1.getMoveText());
        getChildren().add(btn1);
        setHgrow(btn1, Priority.ALWAYS);

        if (m2 != null) {
            Button btn2 = new Button(m2.getMoveText());
            getChildren().add(btn2);
            setHgrow(btn2, Priority.ALWAYS);
        }

    }

}
