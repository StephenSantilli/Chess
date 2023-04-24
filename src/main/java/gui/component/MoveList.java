package gui.component;

import game.Position;
import gui.GameView;

import java.util.ArrayList;

import game.Game;
import game.GameSettings;
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
                    && getRowIndex(c) == calcRow(active + board.getGame().getPositions().get(0).getMoveNumber())
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

        final Game game = board.getGame();
        final GameSettings stgs = board.getGame().getSettings();

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

        final boolean addEllipses = !stgs.getFen().equals(GameSettings.DEFAULT_FEN) && game.getPositions().size() > 0
                && game.getPositions().get(0).getMoveNumber() != 0;

        if (addEllipses) {

            final boolean isWhite = game.getPositions().get(0).isWhite();

            Button ellipses = new Button("...");

            ellipses.setId("movePaneButton");
            ellipses.setMaxWidth(Double.MAX_VALUE);
            add(ellipses, isWhite ? 2 : 1,
                    calcRow(game.getPositions().get(0).getMoveNumber()));

        }

        if (!result.equals("")) {

            Button res = new Button(result);
            res.setId("movePaneButton");
            res.setMaxWidth(Double.MAX_VALUE);

            if (addEllipses) {

                final boolean isWhite = game.getLastPos().isWhite();

                add(res, isWhite ? 1 : 2,
                        calcRow(game.getLastPos().getMoveNumber() + 1));

            } else

                add(res, board.getGame().getLastPos().isWhite() ? 1 : 2,
                        calcRow(game.getLastPos().getMoveNumber() + 1));

            res.requestFocus();

        }

        sp.applyCss();
        sp.layout();

        sp.setVvalue(1);

    }

    private int calcRow(int moveNumber) {

        return (int) Math.ceil((moveNumber /* - 1 */) / 2.0) - 1;

    }

    private void execMove(int pos) {

        Position p = board.getGame().getPositions().get(pos);
        int row = calcRow(p.getMoveNumber());

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
        btn.requestFocus();

        if (m.isWhite() || pos == 1) {

            Label l = new Label((row + 1) + ".");
            l.setId("movePaneLabel");

            add(l, 0, row);

        }

    }

}
