package guifx;

import game.Position;

import java.util.ArrayList;

import game.BoardMoveListener;
import game.Game;
import game.Move;
import javafx.application.Platform;
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

        setMinWidth(220);
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

        setVisible(true);

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

        for (int i = 1; i < g.getPositions().size(); i++) {

            moveMade();

        }

    }

    @Override
    public void moveMade() {

        Platform.runLater(() -> {

            Position p = g.getPositions().get(g.getPositions().size() - 1);
            Move m = p.getMove();
            Button btn1 = new Button(p.getMoveString());
            btn1.setMaxWidth(Double.MAX_VALUE);
            add(btn1, m.isWhite() ? 1 : 2, (g.getPositions().size() / 2));
            GridPane.setMargin(btn1, new Insets(5, 5, 5, 5));

            if (m.isWhite()) {
                Label l = new Label((g.getPositions().size() / 2) + ".");
                add(l, 0, (g.getPositions().size() / 2));
                GridPane.setMargin(l, new Insets(5, 5, 5, 5));
            }

        });

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
