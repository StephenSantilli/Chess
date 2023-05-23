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

/**
 * A pane that displays a list of the moves made in the game.
 */
public class MoveList extends GridPane {

    /**
     * The GameView that contains this move list.
     */
    private GameView gameView;

    /**
     * A list of each row which displays two moves, a white turn and a black turn.
     */
    private ArrayList<MoveRow> rows;

    /**
     * The scroll pane that contains {@link #rows}, so that they are scrollable.
     */
    private ScrollPane sp;

    /**
     * Creates a new pane which displays a list of the moves made in the game.
     * 
     * @param gameView The GameView that contains this.
     * @param sp       The scroll pane that will contain the moves.
     */
    public MoveList(GameView gameView, ScrollPane sp) {

        setId("movePane");

        setMaxWidth(Double.MAX_VALUE);
        this.gameView = gameView;

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

    /**
     * Updates the move list.
     */
    public void boardUpdated() {

        initMoveList();

    }

    /**
     * Called when {@link GameView#currentPos} is changed, but there was no change
     * to the list of moves.
     * 
     * @param active The new active position.
     */
    public void posChanged(int active) {

        for (int i = 0; i < getChildren().size(); i++) {

            Node c = getChildren().get(i);

            if (active != 0
                    && getRowIndex(c) == calcRow(active + gameView.getGame().getPositions().get(0).getMoveNumber())
                    && getColumnIndex(
                            c) == (gameView.getGame().getPositions().get(active).isWhite() ? 2
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

    /**
     * Updates the move pane.
     */
    public void updateMovePane() {

        if (rows == null || rows.size() == 0) {

            initMoveList();
            return;

        }

    }

    /**
     * Initializes the move list from scratch based on the positions in the game.
     */
    public void initMoveList() {

        getChildren().clear();

        if (gameView.getGame() == null)
            return;

        for (int i = 1; i < gameView.getGame().getPositions().size(); i++) {

            execMove(i);

        }

        final Game game = gameView.getGame();
        final GameSettings stgs = gameView.getGame().getSettings();

        String result = "";
        switch (gameView.getGame().getResult()) {
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

                add(res, gameView.getGame().getLastPos().isWhite() ? 1 : 2,
                        calcRow(game.getLastPos().getMoveNumber() + 1));

            res.requestFocus();

        }

        sp.applyCss();
        sp.layout();

        sp.setVvalue(1);

    }

    /**
     * Calculates the move number for a given row.
     */
    private int calcRow(int moveNumber) {

        return (int) Math.ceil((moveNumber /* - 1 */) / 2.0) - 1;

    }

    /**
     * Adds the given move to the move list.
     * 
     * @param pos The index of the position of the move to add to the list.
     */
    private void execMove(int pos) {

        Position p = gameView.getGame().getPositions().get(pos);
        int row = calcRow(p.getMoveNumber());

        Move m = p.getMove();
        Button btn = new Button(p.getMoveString());
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setId("movePaneButton");
        btn.setFocusTraversable(true);

        btn.setOnAction(e -> {

            try {
                gameView.setPos(pos);
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
