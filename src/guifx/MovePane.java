package guifx;

import game.Position;

import java.util.ArrayList;

import game.Game;
import game.Move;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MovePane extends GridPane {

    private Game g;

    private ArrayList<MoveRow> rows;

    public MovePane(Game g) {

        setMinWidth(200);
        this.g = g;

    }

    public void updateMovePane() {

        if (rows == null || rows.size() == 0) {
            initMovePane();
            return;
        }

        MoveRow last = rows.get(rows.size() - 1);
        int mNumber = last.getMoveNumber() * 2 - 1;

        if (last.getM2() == null) {
            rows.remove(rows.size() - 1);
            getChildren().remove(last);
        } else
            mNumber += 2;

        for (int i = mNumber; i < g.getPositions().size(); i += 2) {

            Position p1 = g.getPositions().get(i);

            Position p2 = null;
            if (g.getPositions().size() > (i + 1))
                p2 = g.getPositions().get(i + 1);

            MoveRow mrow = new MoveRow((i / 2) + 1, p1.getMove(), p2 == null ? null : p2.getMove());
            getChildren().add(mrow);
            rows.add(mrow);

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

            Button btn2 = null;
            if (p2 != null)
                btn2 = new Button(p2.getMove().getMoveText());

            add(l, 0, moveNumber - 1);
            GridPane.setFillWidth(l, false);
            GridPane.setHgrow(l, Priority.NEVER);
            GridPane.setMargin(l, new Insets(5, 5, 5, 5));
            GridPane.setHalignment(l, HPos.LEFT);

            add(btn1, 1, moveNumber - 1);
            GridPane.setHgrow(btn1, Priority.ALWAYS);
            GridPane.setHalignment(l, HPos.CENTER);
            GridPane.setFillWidth(btn1, true);
            GridPane.setMargin(btn1, new Insets(5, 5, 5, 5));

            if (btn2 != null) {
                add(btn2, 2, moveNumber - 1);
                GridPane.setFillWidth(btn2, true);
                GridPane.setHalignment(l, HPos.RIGHT);
                GridPane.setHgrow(btn2, Priority.ALWAYS);
                GridPane.setMargin(btn2, new Insets(5, 5, 5, 5));

            }
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(20);
            c.setHgrow(Priority.ALWAYS);
            c.setFillWidth(true);

            ColumnConstraints c1 = new ColumnConstraints();
            c1.setPercentWidth(40);
            c1.setHgrow(Priority.ALWAYS);
            c1.setFillWidth(true);

            ColumnConstraints c2 = new ColumnConstraints();
            c2.setPercentWidth(40);
            c2.setHgrow(Priority.ALWAYS);
            c2.setFillWidth(true);

            // getColumnConstraints().addAll(c, c1, c2);
            // rows.add(mrow);

        }

    }

}
