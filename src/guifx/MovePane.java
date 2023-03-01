package guifx;

import game.Position;

import java.util.ArrayList;

import game.BoardMoveListener;
import game.Game;
import game.Move;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.control.Control;
import javafx.scene.layout.VBox;

public class MovePane extends GridPane implements BoardMoveListener {

    private Game g;

    private ArrayList<MoveRow> rows;

    public MovePane(Game g) {

        setMinWidth(200);
        setMaxWidth(Double.MAX_VALUE);
        this.g = g;

        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(20);
        c.setFillWidth(false);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(40);
        c1.setFillWidth(true);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(40);
        c2.setFillWidth(true);

        getColumnConstraints().clear();
        getColumnConstraints().addAll(c, c1, c2);

    }

    public void updateMovePane() {

        if (rows == null || rows.size() == 0) {
            initMovePane();
            return;
        }

    }

    public void initMovePane() {

        getChildren().clear();
        // rows = new ArrayList<MoveRow>();

        for (int i = 1; i < g.getPositions().size(); i += 2) {

            Position p1 = g.getPositions().get(i);

            Position p2 = null;
            if (g.getPositions().size() > (i + 1))
                p2 = g.getPositions().get(i + 1);

            int moveNumber = (i / 2) + 1;
            Label l = new Label(moveNumber + ".");

            Move m1 = p1.getMove();
            Button btn1 = new Button(m1.getMoveText());
            btn1.setMaxWidth(Double.MAX_VALUE);

            Button btn2 = null;
            if (p2 != null) {
                btn2 = new Button(p2.getMove().getMoveText());
                btn2.setMaxWidth(Double.MAX_VALUE);
            }

            // setGridLinesVisible(true);

            add(l, 0, moveNumber - 1);
            GridPane.setMargin(l, new Insets(5, 5, 5, 5));

            add(btn1, 1, moveNumber - 1);

            GridPane.setMargin(btn1, new Insets(5, 5, 5, 5));

            if (btn2 != null) {
                add(btn2, 2, moveNumber - 1);
                GridPane.setMargin(btn2, new Insets(5, 5, 5, 5));
            }

        }

    }

    @Override
    public void moveMade() {

        Position p = g.getPositions().get(g.getPositions().size() - 1);
        Move m = p.getMove();
        Button btn1 = new Button(m.getMoveText());
        btn1.setMaxWidth(Double.MAX_VALUE);
        add(btn1, m.isWhite() ? 1 : 2, (g.getPositions().size() / 2));
        GridPane.setMargin(btn1, new Insets(5, 5, 5, 5));
        
        
        if()

    }

    @Override
    public void undoMove() {
        getChildren().remove(getChildren().size() - 1);
    }

    @Override
    public void resetMoves() {
        initMovePane();
    }

}
