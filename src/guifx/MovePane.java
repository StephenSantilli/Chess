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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class MovePane extends GridPane implements BoardMoveListener {

    private Game g;

    private ArrayList<MoveRow> rows;

    private int activePos = 0;

    private ScrollPane sp;

    private static final String BUTTON_INACTIVE = "#ffffff";
    private static final String BUTTON_ACTIVE = "#bbbbbb";
    private static final String BUTTON_INACTIVE_HOVER = "#dddddd";
    private static final String BUTTON_ACTIVE_HOVER = "#999999";
    private static final String BUTTON_INACTIVE_CLICKED = "#cacaca";
    private static final String BUTTON_ACTIVE_CLICKED = "#888888";

    public MovePane(Game g, ScrollPane sp) {

        setMinWidth(220);
        setMaxWidth(Double.MAX_VALUE);
        this.g = g;

        this.sp = sp;

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

            execMove(i);

        }

    }

    private void execMove() {
        execMove(g.getPositions().size() - 1);
    }

    private void execMove(int pos) {

        Position p = g.getPositions().get(pos);
        int row = ((pos - 1) / 2);
        Move m = p.getMove();
        Button btn1 = new Button(p.getMoveString());
        btn1.setMaxWidth(Double.MAX_VALUE);
        btn1.setStyle("-fx-background-color: " + BUTTON_INACTIVE);

        btn1.setOnMouseEntered(e -> {
            if (btn1.getStyle().endsWith(BUTTON_INACTIVE)) {
                btn1.setStyle("-fx-background-color: " + BUTTON_INACTIVE_HOVER);
            } else if (btn1.getStyle().endsWith(BUTTON_ACTIVE)) {
                btn1.setStyle("-fx-background-color: " + BUTTON_ACTIVE_HOVER);
            }
        });

        btn1.setOnMouseExited(e -> {
            if (btn1.getStyle().endsWith(BUTTON_INACTIVE_HOVER)) {
                btn1.setStyle("-fx-background-color: " + BUTTON_INACTIVE);
            } else if (btn1.getStyle().endsWith(BUTTON_ACTIVE_HOVER)) {
                btn1.setStyle("-fx-background-color: " + BUTTON_ACTIVE);
            }
        });

        btn1.setFocusTraversable(true);

        btn1.setOnAction(e -> {

            g.setCurrentPos(pos);

        });

        btn1.setOnMousePressed(e -> {
            if (btn1.getStyle().endsWith(BUTTON_INACTIVE) || btn1.getStyle().endsWith(BUTTON_INACTIVE_HOVER)) {
                btn1.setStyle("-fx-background-color: " + BUTTON_INACTIVE_CLICKED);
            }
        });

        btn1.setOnMouseReleased(e -> {
            if (btn1.getStyle().endsWith(BUTTON_INACTIVE_CLICKED)) {
                btn1.setStyle("-fx-background-color: " + BUTTON_INACTIVE);
            }
        });

        add(btn1, m.isWhite() ? 1 : 2, row);
        requestFocus();
        sp.setVvalue(1);

        GridPane.setMargin(btn1, new Insets(5, 5, 5, 5));

        if (m.isWhite()) {
            Label l = new Label((row + 1) + ".");
            add(l, 0, row);
            GridPane.setMargin(l, new Insets(5, 5, 5, 5));
        }

        posChanged(-1, -1);

    }

    @Override
    public void moveMade() {

        Platform.runLater(() -> {
            execMove();
        });

    }

    @Override
    public void undoMove() {

        Platform.runLater(() -> {

            for (int i = 0; i < getChildren().size(); i++) {

                Node c = getChildren().get(i);

                if (getRowIndex(c) == ((g.getCurrentPos()) / 2)) {

                    if (getColumnIndex(c) == (g.getActivePos().getRedo().isWhite() ? 2 : 1)) {
                        getChildren().remove(c);
                        --i;
                    } else if (g.getActivePos().isWhite()
                            && getColumnIndex(c) == 0) {
                        getChildren().remove(c);
                        --i;

                    }

                }

            }

        });
    }

    @Override
    public void resetMoves() {
        initMovePane();
    }

    @Override
    public void posChanged(int old, int curr) {

        Platform.runLater(() -> {

            for (int i = 0; i < getChildren().size(); i++) {

                Node c = getChildren().get(i);

                if (g.getCurrentPos() != 0 && getRowIndex(c) == ((g.getCurrentPos() - 1) / 2)
                        && getColumnIndex(c) == (g.getPositions().get(g.getCurrentPos()).isWhite() ? 2 : 1)) {

                    if (c.isHover()) {
                        c.setStyle("-fx-background-color: " + BUTTON_ACTIVE_HOVER);
                    } else 
                        c.setStyle("-fx-background-color: " + BUTTON_ACTIVE);

                    c.requestFocus();
                    // sp.setVvalue(sp.getViewportBounds().getHeight());

                } else if (activePos > 0 && activePos < g.getPositions().size()
                        && getRowIndex(c) == ((activePos - 1) / 2)
                        && getColumnIndex(c) == (g.getPositions().get(activePos).isWhite() ? 2 : 1)) {

                    if (c.isHover()) {
                        c.setStyle("-fx-background-color: " + BUTTON_INACTIVE_HOVER);
                    } else
                        c.setStyle("-fx-background-color: " + BUTTON_INACTIVE);

                }
                if (g.getCurrentPos() == 0) {
                    sp.setVvalue(0);
                }

            }

            activePos = g.getCurrentPos();

        });

    }

    @Override
    public void redoMove() {
        // TODO Auto-generated method stub

    }

}
