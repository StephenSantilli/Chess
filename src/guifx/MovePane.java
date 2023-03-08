package guifx;

import game.Position;

import java.util.ArrayList;

import game.BoardMoveListener;
import game.Game;
import game.Move;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class MovePane extends GridPane implements BoardMoveListener {

    private Game g;

    private ArrayList<MoveRow> rows;

    private int activePos = 0;

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

            posChanged();
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

    @Override
    public void posChanged() {

        Platform.runLater(() -> {

            for (int i = 0; i < getChildren().size(); i++) {

                Node c = getChildren().get(i);

                if (g.getCurrentPos() != 0 && getRowIndex(c) == (int) Math.ceil(g.getCurrentPos() / 2.0)
                        && getColumnIndex(c) == (g.getPositions().get(g.getCurrentPos()).isWhite() ? 2 : 1)) {

                    c.setStyle("-fx-background-color: #bbbbbb;");

                } else if (activePos > 0 && getRowIndex(c) == (int) Math.ceil(activePos / 2.0)
                        && getColumnIndex(c) == (g.getPositions().get(activePos).isWhite() ? 2 : 1)) {

                    c.setStyle("-fx-background-color: #ffffff;");

                }

            }

            activePos = g.getCurrentPos();

        });

    }

}
