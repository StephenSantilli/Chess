package gui.component;

import game.Move;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class MoveRow extends HBox {

    int moveNumber;
    Move move1, move2;

    public int getMoveNumber() {
        return moveNumber;
    }

    public Move getMove1() {
        return move1;
    }

    public Move getMove2() {
        return move2;
    }

    public MoveRow(int moveNumber, Move m1, Move m2) {

        
        this.moveNumber = moveNumber;
        this.move1 = m1;
        this.move2 = m2;



        Label l = new Label(moveNumber + ".");

        l.setAlignment(Pos.TOP_LEFT);

        HBox.setMargin(l, new Insets(5, 5, 5, 5));
        HBox.setHgrow(l, Priority.ALWAYS);

        HBox moves = new HBox();

        moves.setAlignment(Pos.CENTER);
        
        HBox.setHgrow(moves, Priority.ALWAYS);

        Button btn1 = new Button(m1.getMoveNotation());

        btn1.setAlignment(Pos.CENTER_LEFT);

        HBox.setMargin(btn1, new Insets(5, 5, 5, 5));
        HBox.setHgrow(btn1, Priority.ALWAYS);

        moves.getChildren().add(btn1);

        if (m2 != null) {

            Button btn2 = new Button(m2.getMoveNotation());
            btn2.setAlignment(Pos.CENTER_RIGHT);

            HBox.setMargin(btn2, new Insets(5, 5, 5, 5));
            HBox.setHgrow(btn2, Priority.ALWAYS);

            moves.getChildren().add(btn2);

        }

        getChildren().addAll(l, moves);

    }

}
