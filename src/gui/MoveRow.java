package gui;

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
    Move m1, m2;

    public MoveRow(int moveNumber, Move m1, Move m2) {

        //setMinWidth(200);
        setBackground(new Background(new BackgroundFill(Color.BLACK, null, getInsets())));

        this.moveNumber = moveNumber;
        this.m1 = m1;
        this.m2 = m2;

        Label l = new Label(moveNumber + ".");
        HBox.setMargin(l, new Insets(5, 5, 5, 5));
        l.setBackground(new Background(new BackgroundFill(Color.BLUE, null, getInsets())));

        l.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(l, Priority.ALWAYS);

        HBox moves = new HBox();
        moves.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, getInsets())));
        Button btn1 = new Button(m1.getMoveText());
        HBox.setMargin(btn1, new Insets(5, 5, 5, 5));
        moves.setAlignment(Pos.CENTER);

        
        
        HBox.setHgrow(moves, Priority.ALWAYS);
        HBox.setHgrow(btn1, Priority.ALWAYS);
        btn1.setBackground(new Background(new BackgroundFill(Color.GREEN, null, getInsets())));
        btn1.setAlignment(Pos.CENTER_LEFT);
        moves.getChildren().add(btn1);

        if (m2 != null) {
            Button btn2 = new Button(m2.getMoveText());
            HBox.setMargin(btn2, new Insets(5, 5, 5, 5));
            btn2.setAlignment(Pos.CENTER_RIGHT);
            HBox.setHgrow(btn2, Priority.ALWAYS);
            btn2.setBackground(new Background(new BackgroundFill(Color.RED, null, getInsets())));
            moves.getChildren().add(btn2);

        }
        getChildren().add(l);
        getChildren().add(moves);


    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public Move getM1() {
        return m1;
    }

    public Move getM2() {
        return m2;
    }

}
