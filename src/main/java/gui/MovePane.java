package gui;

import game.Position;

import java.util.ArrayList;

import game.Move;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class MovePane extends GridPane {

    private Board board;

    private ArrayList<MoveRow> rows;

    private ScrollPane sp;

    private static final String BUTTON_INACTIVE = "#ffffff";
    private static final String BUTTON_ACTIVE = "#bbbbbb";
    private static final String BUTTON_INACTIVE_HOVER = "#dddddd";
    private static final String BUTTON_ACTIVE_HOVER = "#999999";
    private static final String BUTTON_INACTIVE_CLICKED = "#cacaca";
    private static final String BUTTON_ACTIVE_CLICKED = "#888888";

    public MovePane(Board board, ScrollPane sp) {

        setId("movePane");

        // setMinWidth(220);
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

        initMovePane();

    }

    public void posChanged(int active) {

        for (int i = 0; i < getChildren().size(); i++) {

            Node c = getChildren().get(i);

            if (active != 0
                    && getRowIndex(c) == ((active - 1) / 2)
                    && getColumnIndex(
                            c) == (board.getGame().getPositions().get(active).isWhite() ? 2
                                    : 1)) {

                // if (c.isHover())
                // c.setStyle("-fx-background-color: " + BUTTON_ACTIVE_HOVER);
                // else
                // c.setStyle("-fx-background-color: " + BUTTON_ACTIVE);
                c.setId("movePaneButtonActive");

                c.requestFocus();

            } else if (getColumnIndex(c) != 0) {
                c.setId("movePaneButton");

                // if (c.isHover())
                // c.setStyle("-fx-background-color: " + BUTTON_INACTIVE_HOVER);
                // else
                // c.setStyle("-fx-background-color: " + BUTTON_INACTIVE);

            }

            if (active == 0) {
                sp.setVvalue(0);
            }

        }

    }

    public void updateMovePane() {

        if (rows == null || rows.size() == 0) {

            initMovePane();
            return;

        }

    }

    public void initMovePane() {

        getChildren().clear();

        if (board.getGame() == null)
            return;

        for (int i = 1; i < board.getGame().getPositions().size(); i++) {

            execMove(i);

        }

    }

    private void execMove(int pos) {

        Position p = board.getGame().getPositions().get(pos);
        int row = ((pos - 1) / 2);
        Move m = p.getMove();
        Button btn = new Button(p.getMoveString());
        btn.setMaxWidth(Double.MAX_VALUE);
        // btn.setStyle("-fx-background-color: " + (board.getCurrentPos() == pos ?
        // BUTTON_ACTIVE : BUTTON_INACTIVE));
        btn.setId("movePaneButton");

        /*
         * btn.setOnMouseEntered(e -> {
         * 
         * if (btn.getStyle().endsWith(BUTTON_INACTIVE))
         * btn.setStyle("-fx-background-color: " + BUTTON_INACTIVE_HOVER);
         * else if (btn.getStyle().endsWith(BUTTON_ACTIVE))
         * btn.setStyle("-fx-background-color: " + BUTTON_ACTIVE_HOVER);
         * 
         * });
         * 
         * btn.setOnMouseExited(e -> {
         * if (btn.getStyle().endsWith(BUTTON_INACTIVE_HOVER)) {
         * btn.setStyle("-fx-background-color: " + BUTTON_INACTIVE);
         * } else if (btn.getStyle().endsWith(BUTTON_ACTIVE_HOVER)) {
         * btn.setStyle("-fx-background-color: " + BUTTON_ACTIVE);
         * }
         * });
         */

        btn.setFocusTraversable(true);

        btn.setOnAction(e -> {

            try {
                board.setCurrentPos(pos);
            } catch (Exception e1) {
            }

        });

        // btn.setOnMousePressed(e -> {
        // if (btn.getStyle().endsWith(BUTTON_INACTIVE) ||
        // btn.getStyle().endsWith(BUTTON_INACTIVE_HOVER)) {
        // btn.setId("movePaneButtonClicked");
        // }
        // });

        // btn.setOnMouseReleased(e -> {
        // if (btn.getStyle().endsWith(BUTTON_INACTIVE_CLICKED)) {
        // btn.setStyle("-fx-background-color: " + BUTTON_INACTIVE);
        // }
        // });

        add(btn, m.isWhite() ? 1 : 2, row);
        requestFocus();
        sp.setVvalue(1);

        // GridPane.setMargin(btn, new Insets(5, 5, 5, 5));

        if (m.isWhite()) {

            Label l = new Label((row + 1) + ".");
            l.setId("movePaneLabel");

            add(l, 0, row);
            // GridPane.setMargin(l, new Insets(5, 5, 5, 5));

        }

    }

}
