package gui.board;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import game.Position;
import game.Square;
import gui.GameView;
import gui.dialog.Promote;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;

public class Board extends StackPane {

    private static final double pieceSizeMultiplier = .85;

    private GameView gameView;

    private Squares squarePane;
    private Highlights highlightPane;
    private SquareBorders borderPane;
    private MoveIndicators moveIndicatorsPane;
    private Pieces piecePane;

    private double squareSize = 100;
    private double pieceSize = Math.round(squareSize * pieceSizeMultiplier);

    private GUIPiece active;
    private GUIPiece dragging;

    private Bounds boardBounds;

    private Timer resizer = new Timer("Resizer", true);
    private TimerTask resizeTask = null;

    private final ChangeListener<Number> resizeEvent = (obs, o, n) -> {

        if (resizeTask != null) {
            resizeTask.cancel();
        }

        resizeTask = new TimerTask() {
            public void run() {
                Platform.runLater(() -> {

                    squareSize = Math.min(gameView.getBoardPane().getWidth(), gameView.getBoardPane().getHeight())
                            / 8.0;
                    pieceSize = Math.round(squareSize * pieceSizeMultiplier);

                    setMaxSize(squareSize * 8, squareSize * 8);

                    boardBounds = localToScene(new BoundingBox(0, 0, squareSize * 8, squareSize * 8));

                    try {

                        squarePane.draw();
                        piecePane.initPieceTranscoders();
                        boardUpdated();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        };

        resizer.schedule(resizeTask, 50);

    };
    private EventHandler<MouseEvent> mouseMoved = ev -> {

        setMouseType(ev.getSceneX(), ev.getSceneY());

    };

    private EventHandler<MouseEvent> mouseReleased = e -> {

        if (gameView.getGame() == null)
            return;

        if (e.getButton() != MouseButton.PRIMARY)
            return;

        if (dragging != null) {
            dragging.onMouseReleased(e);
        } else {

            GUIPiece found = getGUIPieceAtSquare(getSquareByLoc((int) e.getSceneX(), (int) e.getSceneY(), true));
            if (found != null)
                found.onMouseReleased(e);
            else if (active != null)
                active.onMouseReleased(e);
            else {

                setActive(null);
                setDragging(null);
                activeUpdated();
                borderPane.drawBorder(null);

            }

        }

        setMouseType(e.getSceneX(), e.getSceneY());

    };

    private EventHandler<MouseEvent> mousePressed = ev -> {

        if (gameView.getGame() == null)
            return;

        if (ev.getButton() != MouseButton.PRIMARY)
            return;

        if (dragging != null) {

            dragging.onMousePressed(ev);

        } else {

            GUIPiece found = getGUIPieceAtSquare(getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY(), true));

            if (found != null)
                found.onMousePressed(ev);
            else if (active != null)
                active.onMousePressed(ev);

        }
        setMouseType(ev.getSceneX(), ev.getSceneY());

    };

    private EventHandler<MouseEvent> mouseDragged = ev -> {

        if (gameView.getGame() == null)
            return;

        if (ev.getButton() != MouseButton.PRIMARY)
            return;

        if (dragging != null) {

            dragging.onMouseDragged(ev);

        }

    };

    public GameView getGameView() {
        return gameView;
    }

    public Squares getSquarePane() {
        return squarePane;
    }

    public void setSquarePane(Squares squarePane) {
        this.squarePane = squarePane;
    }

    public Highlights getHighlightPane() {
        return highlightPane;
    }

    public void setHighlightPane(Highlights highlightPane) {
        this.highlightPane = highlightPane;
    }

    public SquareBorders getBorderPane() {
        return borderPane;
    }

    public void setBorderPane(SquareBorders borderPane) {
        this.borderPane = borderPane;
    }

    public MoveIndicators getMoveIndicatorsPane() {
        return moveIndicatorsPane;
    }

    public void setMovesCanvas(MoveIndicators moveIndicatorsPane) {
        this.moveIndicatorsPane = moveIndicatorsPane;
    }

    public Pieces getPiecePane() {
        return piecePane;
    }

    public void setPiecePane(Pieces piecePane) {
        this.piecePane = piecePane;
    }

    public double getSquareSize() {
        return squareSize;
    }

    public void setSquareSize(double squareSize) {
        this.squareSize = squareSize;
    }

    public double getPieceSize() {
        return pieceSize;
    }

    public void setPieceSize(double pieceSize) {
        this.pieceSize = pieceSize;
    }

    public GUIPiece getActive() {
        return active;
    }

    public void setActive(GUIPiece active) {
        this.active = active;
    }

    public GUIPiece getDragging() {
        return dragging;
    }

    public void setDragging(GUIPiece dragging) {
        this.dragging = dragging;
    }

    public Bounds getBoardBounds() {
        return boardBounds;
    }

    public void setBoardBounds(Bounds boardBounds) {
        this.boardBounds = boardBounds;
    }

    public ChangeListener<Number> getResizeEvent() {
        return resizeEvent;
    }

    public EventHandler<MouseEvent> getMouseMoved() {
        return mouseMoved;
    }

    public EventHandler<MouseEvent> getMouseReleased() {
        return mouseReleased;
    }

    public EventHandler<MouseEvent> getMousePressed() {
        return mousePressed;
    }

    public EventHandler<MouseEvent> getMouseDragged() {
        return mouseDragged;
    }

    public Board(GameView gameView) {

        this.gameView = gameView;

        squarePane = new Squares(gameView);
        highlightPane = new Highlights(gameView);
        borderPane = new SquareBorders(gameView);
        moveIndicatorsPane = new MoveIndicators(gameView);
        piecePane = new Pieces(gameView);

        getChildren().addAll(squarePane, highlightPane, moveIndicatorsPane, borderPane, piecePane);

        gameView.getApp().getStage().addEventHandler(WindowEvent.WINDOW_SHOWN, (we -> {

            try {
                squarePane.draw();
                piecePane.initPieceTranscoders();
            } catch (Exception e) {
            }

        }));

    }

    public void clearSelection() {

        setActive(null);
        setDragging(null);
        borderPane.drawBorder(null);
        boardUpdated();

    }

    public void showPromoteDialog(Square square, boolean white, GUIPiece callback) throws Exception {

        Promote pD = new Promote(pieceSize, squareSize, white, gameView.isFlipped(),
                getScene().getWindow());

        pD.setOnHidden(ev -> {

            callback.setPromoteResponse(pD.getResult());
            callback.getPromoteCallback().run();

        });

        Bounds bds = getPiecePane().localToScreen(getPiecePane().getBoundsInLocal());

        pD.setX(bds.getMinX() + getXBySquare(square));
        pD.setY(bds.getMinY() + getYBySquare(square)
                - ((!white && gameView.isFlipped()) || (white && !gameView.isFlipped())
                        ? (-squareSize)
                        : (squareSize) * (4 + (1 / 3.0))));

        pD.show();
        pD.sizeToScene();

    }

    /**
     * Updates the square highlights and moves panes.
     */
    public void activeUpdated() {

        highlightPane.draw();
        moveIndicatorsPane.draw();

    }

    public void boardUpdated() {
        boardUpdated(false, null, null, false);
    }

    public void boardUpdated(boolean animate, Position p1, Position p2, boolean backward) {

        if (gameView.getGame() == null) {

            borderPane.drawBorder(null);
            activeUpdated();

            gameView.getGameMenu().update();
            gameView.getViewMenu().update();

            gameView.getInfoPane().updateTimers();

            gameView.getMoveList().initMoveList();

            gameView.getInfoPane().getTopName().setText("");
            gameView.getInfoPane().getBottomName().setText("");

            active = null;
            dragging = null;
            piecePane.draw();

            return;

        }

        boolean ani = animate && dragging == null;
        dragging = null;

        if (gameView.getGame().getLastPos().getMove() != null
                && gameView.getGame().getLastPos().getMove().getPromoteType() != '0'
                && (gameView.getColor() == GameView.TWO_PLAYER
                        || gameView.getGame().getLastPos().isWhite() != (gameView.getColor() == GameView.WHITE)))
            ani = false;

        piecePane.draw(backward, ani ? p1 : null, p2);

        gameView.getInfoPane().getTopTimer().setWhite(gameView.isFlipped());
        gameView.getInfoPane().getBottomTimer().setWhite(!gameView.isFlipped());

        gameView.getInfoPane().updateTimers();

        gameView.getInfoPane().getTopName().setText(gameView.getGame().getPlayer(gameView.isFlipped()).getName());
        gameView.getInfoPane().getBottomName().setText(gameView.getGame().getPlayer(!gameView.isFlipped()).getName());

        activeUpdated();

        gameView.getGameMenu().update();
        gameView.getViewMenu().update();

    }

    public void setMouseType(double mouseX, double mouseY) {

        if (gameView.getGame() == null) {
            setCursor(Cursor.DEFAULT);
            return;
        }

        if (dragging != null) {

            setCursor(Cursor.CLOSED_HAND);

        } else if (getSquareByLoc(mouseX, mouseY, true).isValid()
                && gameView.getGame().getPositions().get(gameView.getCurrentPos())
                        .getPieceAtSquare(getSquareByLoc(mouseX, mouseY, true)) != null) {

            setCursor(Cursor.OPEN_HAND);

        } else {
            setCursor(Cursor.DEFAULT);
        }

    }

    // Calculations

    public GUIPiece getGUIPieceAtSquare(Square square) {

        GUIPiece found = null;

        final ArrayList<GUIPiece> pieces = piecePane.getPieces();

        for (int i = 0; i < pieces.size() && found == null; i++) {

            if (pieces.get(i).getPiece().getSquare().equals(square))
                found = pieces.get(i);

        }

        return found;

    }

    /**
     * Gets the {@link Square} that {@code x} and {@code y} fall within.
     * 
     * <p>
     * If {@code relative} is {@code true}, the {@code x} and {@code y} values will
     * be treated as coming from the outer {@link VBox} that encases the
     * {@link GameView}.
     * 
     * @param x        The x location
     * @param y        The y location
     * @param relative Whether or not to treat the {@code x} and {@code y} values as
     *                 relative values
     * @return The {@link Square} object
     */
    public Square getSquareByLoc(double x, double y, boolean relative) {

        if (relative) {
            x -= boardBounds.getMinX();
            y -= boardBounds.getMinY();
        }

        Square sq = null;

        if (!gameView.isFlipped())
            sq = new Square((int) (x / squareSize) + 1, 8 - (int) (y / squareSize));
        else
            sq = new Square(8 - (int) (x / squareSize), (int) (y / squareSize) + 1);

        if (!sq.isValid())
            return new Square(-1, -1);
        else
            return sq;
    }

    /**
     * Gets the x location of the provided {@link Square}.
     * 
     * @param square The {@link Square} to get the x value for
     * @return The x location of the top left of the {@link Square}
     */
    public double getXBySquare(Square square) {

        return getXBySquare(square, false);

    }

    /**
     * Gets the x location of the provided {@link Square}.
     * 
     * <p>
     * If {@code relative} is {@code true}, the x value will be in the context of
     * the outer {@link VBox} that encases the {@link GameView}.
     * 
     * @param square   The {@link Square} to get the x value for
     * @param relative Whether or not to treat the x value as relative
     * @return The x location of the top left of the {@link Square}
     */
    public double getXBySquare(Square square, boolean relative) {

        double rel = 0;

        if (relative)
            rel = boardBounds.getMinX();

        if (!gameView.isFlipped())
            return ((square.getFile() - 1) * squareSize) - rel;
        else
            return ((squareSize * 7) - ((square.getFile() - 1) * squareSize)) - rel;

    }

    /**
     * Gets the y location of the provided {@link Square}.
     * 
     * @param square The {@link Square} to get the y value for
     * @return The y location of the top left of the {@link Square}
     */
    public double getYBySquare(Square square) {

        return getYBySquare(square, false);

    }

    /**
     * Gets the y location of the provided {@link Square}.
     * 
     * <p>
     * If {@code relative} is {@code true}, the y value will be in the context of
     * the outer {@link VBox} that encases the {@link GameView}.
     * 
     * @param square   The {@link Square} to get the y value for
     * @param relative Whether or not to treat the y value as relative
     * @return The y location of the top left of the {@link Square}
     */
    public double getYBySquare(Square square, boolean relative) {

        double rel = 0;

        if (relative)
            rel = boardBounds.getMinY();

        if (!gameView.isFlipped())
            return ((squareSize * 7) - ((square.getRank() - 1) * squareSize)) - rel;
        else
            return ((square.getRank() - 1) * squareSize) - rel;

    }

    public static String getSquareCornerRadius(Square square) {

        if (square.getFile() == 1 && square.getRank() == 1)
            return "0 0 0 10";
        else if (square.getFile() == 8 && square.getRank() == 1)
            return "0 0 10 0";
        else if (square.getFile() == 8 && square.getRank() == 8)
            return "0 10 0 0";
        else if (square.getFile() == 1 && square.getRank() == 8)
            return "10 0 0 0";

        return "0 0 0 0";

    }

}
