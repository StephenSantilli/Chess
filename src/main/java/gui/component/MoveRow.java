package gui.component;

import game.Move;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * A row representing a black move and a white move in the {@link MoveList}.
 */
public class MoveRow extends HBox {

    /**
     * The amount of full turns (black and white moved), starting from 1.
     */
    private int moveNumber;

    /**
     * The move that will be displayed on the left of the row.
     */
    private Move move1;

    /**
     * The move that will be displayed on the right of the row.
     */
    private Move move2;

    /**
     * Creates a new move row.
     * 
     * @param moveNumber The number of {@code move1}.
     * @param move1      The first move.
     * @param move2      The second move.
     */
    public MoveRow(int moveNumber, Move move1, Move move2) {

        this.moveNumber = moveNumber;
        this.move1 = move1;
        this.move2 = move2;

        Label l = new Label(moveNumber + ".");

        l.setAlignment(Pos.TOP_LEFT);

        HBox.setMargin(l, new Insets(5, 5, 5, 5));
        HBox.setHgrow(l, Priority.ALWAYS);

        HBox moves = new HBox();
        moves.setAlignment(Pos.CENTER);

        HBox.setHgrow(moves, Priority.ALWAYS);

        Button btn1 = new Button(move1.getMoveNotation());
        btn1.setAlignment(Pos.CENTER_LEFT);

        HBox.setMargin(btn1, new Insets(5, 5, 5, 5));
        HBox.setHgrow(btn1, Priority.ALWAYS);

        moves.getChildren().add(btn1);

        if (move2 != null) {

            Button btn2 = new Button(move2.getMoveNotation());
            btn2.setAlignment(Pos.CENTER_RIGHT);

            HBox.setMargin(btn2, new Insets(5, 5, 5, 5));
            HBox.setHgrow(btn2, Priority.ALWAYS);

            moves.getChildren().add(btn2);

        }

        getChildren().addAll(l, moves);

    }

    /**
     * Gets the move number.
     * 
     * @return {@link #moveNumber}
     */
    public int getMoveNumber() {
        return moveNumber;
    }

    /**
     * Gets the first move.
     * 
     * @return {@link #move1}
     */
    public Move getMove1() {
        return move1;
    }

    /**
     * Gets the second move.
     * 
     * @return {@link #move2}
     */
    public Move getMove2() {
        return move2;
    }

}
