package gui.component;

import game.Position;
import gui.GameView;

import java.util.ArrayList;

import game.Move;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class MoveList extends GridPane {

    private GameView board;

    private ArrayList<MoveRow> rows;

    private ScrollPane sp;

    public MoveList(GameView board, ScrollPane sp) {

        setId("movePane");

        setMaxWidth(Double.MAX_VALUE);
        this.board = board;

        this.sp = sp;

        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(15);
        c.setFillWidth(false);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(42.5);
        c1.setFillWidth(true);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(42.5);
        c2.setFillWidth(true);

        getColumnConstraints().clear();
        getColumnConstraints().addAll(c, c1, c2);

        setVisible(true);

    }

    public void boardUpdated() {

        initMoveList();

    }

    public void posChanged(int active) {

        for (int i = 0; i < getChildren().size(); i++) {

            Node c = getChildren().get(i);

            if (active != 0
                    && getRowIndex(c) == ((active - 1) / 2)
                    && getColumnIndex(
                            c) == (board.getGame().getPositions().get(active).isWhite() ? 2
                                    : 1)) {

                c.setId("movePaneButtonActive");

                c.requestFocus();

            } else if (getColumnIndex(c) != 0) {
                c.setId("movePaneButton");

            }

            if (active == 0) {
                sp.setVvalue(0);
            }

        }

    }

    public void updateMovePane() {

        if (rows == null || rows.size() == 0) {

            initMoveList();
            return;

        }

    }

    public void initMoveList() {

        getChildren().clear();

        if (board.getGame() == null)
            return;

        for (int i = 1; i < board.getGame().getPositions().size(); i++) {

            execMove(i);

        }

        String result = "";
        switch (board.getGame().getResult()) {
            case DRAW:
                result = "1/2-1/2";
                break;
            case WHITE_WIN:
                result = "1-0";
                break;
            case BLACK_WIN:
                result = "0-1";
                break;
            default:
                break;
        }

        if (!result.equals("")) {

            Button res = new Button(result);
            res.setId("movePaneButton");
            res.setMaxWidth(Double.MAX_VALUE);
            add(res, board.getGame().getLastPos().isWhite() ? 1 : 2,
                    (int) Math.ceil((board.getGame().getLastPos().getMoveNumber() + 1) / 2.0) - 1);

        }

        sp.applyCss();
        sp.layout();

        sp.setVvalue(1);

    }

    private void execMove(int pos) {

        Position p = board.getGame().getPositions().get(pos);
        int row = (int) Math.ceil((p.getMoveNumber()) / 2.0) - 1;
        Move m = p.getMove();
        Button btn = new Button(p.getMoveString());
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setId("movePaneButton");
        btn.setFocusTraversable(true);

        btn.setOnAction(e -> {

            try {
                board.setPos(pos);
            } catch (Exception e1) {
            }

        });

        add(btn, m.isWhite() ? 1 : 2, row);
        requestFocus();

        if (m.isWhite() || pos == 1) {

            Label l = new Label((row + 1) + ".");
            l.setId("movePaneLabel");

            add(l, 0, row);

        }

    }

}
