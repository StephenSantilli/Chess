package gui.board;

import java.util.ArrayList;

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
    private Coordinates coordsPane;
    private SquareBorders borderPane;
    private MoveIndicators moveIndicatorsPane;
    private Pieces piecePane;
    private PauseView pausePane;

    private double squareSize = 100;
    private double pieceSize = Math.round(squareSize * pieceSizeMultiplier);

    private GUIPiece active;
    private GUIPiece dragging;

    private Bounds boardBounds;

    private MouseEvent resizing;

    private final ChangeListener<Number> resizeEvent = (obs, o, n) -> {

        Platform.runLater(() -> {

            boardBounds = localToScene(new BoundingBox(0, 0, squareSize * 8, squareSize * 8));

        });

    };

    private final EventHandler<MouseEvent> mouseMoved = ev -> {

        setMouseType(ev.getSceneX(), ev.getSceneY());

    };

    private final EventHandler<MouseEvent> mousePressed = ev -> {

        if (ev.getButton() != MouseButton.PRIMARY)
            return;

        if (isResizingBounds(ev.getSceneX(), ev.getSceneY())) {

            resizing = ev;
            setMouseType(ev.getSceneX(), ev.getSceneY());

            return;

        }

        if (gameView.getGame() == null
                || gameView.getGame().isPaused())
            return;

        if (dragging != null) {

            dragging.onMousePressed(ev);

        } else {

            final GUIPiece found = getGUIPieceAtSquare(
                    getSquareByPoint(ev.getSceneX(), ev.getSceneY(), true));

            if (found != null)
                found.onMousePressed(ev);
            else if (active != null)
                active.onMousePressed(ev);

        }

        setMouseType(ev.getSceneX(), ev.getSceneY());

    };

    private final EventHandler<MouseEvent> mouseDragged = ev -> {

        if (ev.getButton() != MouseButton.PRIMARY)
            return;

        if (resizing != null) {

            // TODO: calculate the padding values, not just default to 280 and 40
            squareSize = Math.min(
                    Math.min(Math.max(20, ev.getSceneX()), getScene().getWidth() - 280),
                    Math.min(Math.max(20, ev.getSceneY()), getScene().getHeight() - 40))
                    / 8.0;
            pieceSize = Math.round(squareSize * pieceSizeMultiplier);

            setMinSize(squareSize * 8, squareSize * 8);
            setMaxSize(squareSize * 8, squareSize * 8);
            setPrefSize(squareSize * 8, squareSize * 8);

            setWidth(squareSize * 8);
            setHeight(squareSize * 8);

            boardBounds = localToScene(new BoundingBox(0, 0, squareSize * 8, squareSize * 8));

            squarePane.draw();
            coordsPane.draw();

            highlightPane.setVisible(false);
            borderPane.setVisible(false);
            moveIndicatorsPane.setVisible(false);
            piecePane.setVisible(false);

            return;

        }

        if (gameView.getGame() == null
                || gameView.getGame().isPaused())
            return;

        if (dragging != null) {

            dragging.onMouseDragged(ev);

        }

    };

    private final EventHandler<MouseEvent> mouseReleased = e -> {

        if (e.getButton() != MouseButton.PRIMARY)
            return;

        if (resizing != null) {

            resizing = null;

            try {

                highlightPane.setVisible(true);
                borderPane.setVisible(true);
                moveIndicatorsPane.setVisible(true);
                piecePane.setVisible(true);
                piecePane.initPieceTranscoders();
                draw();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return;
        }

        if (gameView.getGame() == null
                || gameView.getGame().isPaused())
            return;

        if (dragging != null) {

            dragging.onMouseReleased(e);

        } else {

            final GUIPiece found = getGUIPieceAtSquare(getSquareByPoint(e.getSceneX(), e.getSceneY(),
                    true));

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

    public PauseView getPausePane() {
        return pausePane;
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
        coordsPane = new Coordinates(gameView);
        borderPane = new SquareBorders(gameView);
        moveIndicatorsPane = new MoveIndicators(gameView);
        piecePane = new Pieces(gameView);
        pausePane = new PauseView();

        pausePane.setVisible(false);

        getChildren().addAll(squarePane, highlightPane, coordsPane, moveIndicatorsPane, borderPane, piecePane,
                pausePane);

        gameView.getApp().getStage().addEventHandler(WindowEvent.WINDOW_SHOWN, (we -> {

            try {

                squarePane.draw();
                coordsPane.draw();

                piecePane.initPieceTranscoders();

            } catch (Exception e) {
            }

        }));

    }

    /**
     * Draws the board from the current position. Will not animate any changes made.
     */
    public void draw() {
        draw(false, null, null, false);
    }

    /**
     * Draws the board view.
     * 
     * @param animate  Whether or not the change between {@code p1} and {@code p2}
     *                 should be animated.
     * @param p1       The current position drawn.
     * @param p2       The new position to be drawn.
     * @param backward Whether or not the change between the two positions is
     *                 forwards or backwards.
     */
    public void draw(boolean animate, Position p1, Position p2, boolean backward) {

        if (gameView.getGame() == null) {

            borderPane.drawBorder(null);
            activeUpdated();

            gameView.getGameMenu().update();
            gameView.getViewMenu().update();

            gameView.getInfoPane().updateTimers();

            gameView.getMoveList().initMoveList();

            gameView.getInfoPane().getTopName().setText("");
            gameView.getInfoPane().getBottomName().setText("");

            setActive(null);
            setDragging(null);

            piecePane.draw();

            return;

        }

        boolean ani = animate && dragging == null;
        dragging = null;

        // Don't animate when promotion move (if you did it/two player game)
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

        borderPane.drawBorder(null);

        activeUpdated();

        gameView.getGameMenu().update();
        gameView.getViewMenu().update();

    }

    /**
     * Clears the active piece and the piece currently being dragged.
     */
    public void clearSelection() {

        setActive(null);
        setDragging(null);

        borderPane.drawBorder(null);

        draw();

    }

    /**
     * Updates the square highlights and moves indicator panes.
     */
    public void activeUpdated() {

        highlightPane.draw();
        moveIndicatorsPane.draw();

    }

    /**
     * Shows the promote choice dialog. The user will be prompted to pick between a
     * queen, rook, bishop, or knight of their own color.
     * 
     * @param square   The {@link Square} to draw the dialog at.
     * @param white    Whether or not the pieces should be white.
     * @param callback The {@link GUIPiece} to call and provide with the results
     *                 when the user has made a selection.
     * @throws Exception When there is an error from the Promote dialog.
     */
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
     * Sets the mouse type based on the mouse position.
     * 
     * @param mouseX The x position of the mouse relative to the scene.
     * @param mouseY The y position of the mouse relative to the scene.
     */
    public void setMouseType(double mouseX, double mouseY) {

        if (resizing != null || isResizingBounds(mouseX, mouseY)) {

            setCursor(Cursor.SE_RESIZE);
            return;

        }

        if (gameView.getGame() == null || gameView.getGame().isPaused()) {

            setCursor(Cursor.DEFAULT);

        } else if (dragging != null) {

            setCursor(Cursor.CLOSED_HAND);

        } else if (getSquareByPoint(mouseX, mouseY, true).isValid()
                && gameView.getGame().getPositions().get(gameView.getCurrentPos())
                        .getPieceAtSquare(getSquareByPoint(mouseX, mouseY, true)) != null) {

            setCursor(Cursor.OPEN_HAND);

        } else {
            setCursor(Cursor.DEFAULT);
        }

    }

    // Calculations

    /**
     * Finds the {@link GUIPiece} at a given square.
     * 
     * @param square The {@link Square} to search at.
     * @return The {@link GUIPiece} found at the square. Will return {@code null} if
     *         no piece found.
     */
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
     * Checks whether or not the mouse is within the resizing area (bottom right
     * corner of the board.)
     * 
     * @param x The x location of the mouse relative to the scene.
     * @param y The y location of the mouse relative to the scene.
     * @return whether or not the mouse is within the resizing corner.
     */
    public boolean isResizingBounds(double x, double y) {

        final double resizeSize = 10;

        return (x >= boardBounds.getMaxX() - resizeSize && x <= boardBounds.getMaxX() + resizeSize
                && y >= boardBounds.getMaxY() - resizeSize && y <= boardBounds.getMaxY() + resizeSize);

    }

    /**
     * Gets the value of the -fx-background-radius value for each square, such that
     * only corner squares will have rounded edges.
     * 
     * @param square The square to use.
     */
    public static String getSquareCornerRadius(Square square) {

        final double cornerRadius = 10;

        if (square.getFile() == 1 && square.getRank() == 1)
            return "0 0 0 " + cornerRadius;
        else if (square.getFile() == 8 && square.getRank() == 1)
            return "0 0 " + cornerRadius + " 0";
        else if (square.getFile() == 8 && square.getRank() == 8)
            return "0 " + cornerRadius + " 0 0";
        else if (square.getFile() == 1 && square.getRank() == 8)
            return cornerRadius + " 0 0 0";

        return "0 0 0 0";

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
    public Square getSquareByPoint(double x, double y, boolean relative) {

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

}
