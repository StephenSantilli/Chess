package gui.board;

import java.util.ArrayList;

import game.Position;
import game.Square;
import gui.GameView;
import gui.board.element.GUIPiece;
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

/**
 * A representation of a chess board.
 */
public class Board extends StackPane {

    /**
     * The multiplier which determines how large the squares should be based on the
     * size of the pieces.
     */
    private static final double pieceSizeMultiplier = .85;

    /**
     * Gets the value of the -fx-background-radius value for each square, such that
     * only corner squares will have rounded edges.
     * 
     * @param square  The square to use.
     * @param flipped If the board is flipped or not.
     * @return the CSS value in order to draw the border radii.
     */
    public static String getSquareCornerRadius(Square square, boolean flipped) {

        final double cornerRadius = 10;

        if (!flipped) {
            if (square.getFile() == 1 && square.getRank() == 1)
                return "0 0 0 " + cornerRadius;
            else if (square.getFile() == 8 && square.getRank() == 1)
                return "0 0 " + cornerRadius + " 0";
            else if (square.getFile() == 8 && square.getRank() == 8)
                return "0 " + cornerRadius + " 0 0";
            else if (square.getFile() == 1 && square.getRank() == 8)
                return cornerRadius + " 0 0 0";
        } else {
            if (square.getFile() == 1 && square.getRank() == 1)
                return "0 " + cornerRadius + " 0 0";
            else if (square.getFile() == 8 && square.getRank() == 1)
                return cornerRadius + " 0 0 0";
            else if (square.getFile() == 8 && square.getRank() == 8)
                return "0 0 0 " + cornerRadius;
            else if (square.getFile() == 1 && square.getRank() == 8)
                return "0 0 " + cornerRadius + " 0";
        }

        return "0 0 0 0";

    }

    /**
     * The GameView that contains this board.
     */
    private GameView gameView;

    /**
     * The pane which displays the underlying squares, with their checkerboard
     * pattern.
     */
    private Squares squarePane;

    /**
     * The pane which displays square highlights.
     */
    private Highlights highlightPane;

    /**
     * The pane which displays the coordinate guides on the bottom rows and left
     * columns.
     */
    private Coordinates coordsPane;

    /**
     * The pane which shows the moves that are possible when a piece is active.
     */
    private MoveIndicators moveIndicatorsPane;

    /**
     * The pane which draws the squares around the border that a piece is currently
     * being dragged over.
     */
    private SquareBorders borderPane;

    /**
     * The pane which displays the pieces.
     */
    private Pieces piecePane;

    /**
     * The pane which displays the highlight arrows.
     */
    private Arrows arrowPane;

    /**
     * The pane which indicates when the game is paused.
     */
    private PauseView pausePane;

    /**
     * The height and width of the pieces, in pixels.
     */
    private double pieceSize = 90;

    /**
     * The height and width of the squares, in pixels.
     */
    private double squareSize = Math.round(pieceSize / pieceSizeMultiplier);

    /**
     * The currently active piece.
     */
    private GUIPiece active;

    /**
     * The piece that is currently being dragged.
     */
    private GUIPiece dragging;

    /**
     * The layout bounds of the board in its scene.
     */
    private Bounds boardBounds;

    /**
     * The mouse event where the user first clicked to start resizing. Will be
     * {@code null} when the user is not currently resizing the board.
     */
    private MouseEvent resizing;

    /**
     * The mouse event where the user first clicked to start drawing a highlight
     * arrow. Will be {@code null} when the user is not currently drawing an arrow.
     */
    private MouseEvent arrowing;

    /**
     * The event that is triggered when the board is resized.
     */
    private final ChangeListener<Number> resizeEvent = (obs, o, n) -> {

        Platform.runLater(() -> {

            boardBounds = localToScene(new BoundingBox(0, 0, squareSize * 8, squareSize * 8));
            gameView.getOpeningLabelPane().update();

        });

    };

    /**
     * The event that is triggered when the mouse is moved.
     */
    private final EventHandler<MouseEvent> mouseMoved = ev -> {

        setMouseType(ev.getSceneX(), ev.getSceneY());

    };

    /**
     * The event that is triggered when the mouse is pressed down.
     */
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

    /**
     * The event that is triggered when the mouse is dragged (meaning the mouse is
     * moving while the button is down.)
     */
    private final EventHandler<MouseEvent> mouseDragged = ev -> {

        if (ev.getButton() == MouseButton.SECONDARY && arrowing == null) {

            arrowing = ev;

            return;

        }

        if (ev.getButton() != MouseButton.PRIMARY)
            return;

        if (resizing != null) {

            squareSize = Math.min(
                    Math.min(Math.max(150, ev.getSceneX()), getScene().getWidth() - 280),
                    Math.min(Math.max(150, ev.getSceneY()), getScene().getHeight() - 40))
                    / 8.0;
            pieceSize = Math.round(squareSize * pieceSizeMultiplier);

            setMinSize(squareSize * 8, squareSize * 8);
            setMaxSize(squareSize * 8, squareSize * 8);
            setPrefSize(squareSize * 8, squareSize * 8);

            setWidth(squareSize * 8);
            setHeight(squareSize * 8);

            boardBounds = localToScene(new BoundingBox(0, 0, squareSize * 8, squareSize * 8));
            gameView.getOpeningLabelPane().update();

            squarePane.draw();
            coordsPane.draw();

            highlightPane.setVisible(false);
            arrowPane.setVisible(false);
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

    /**
     * The event that is triggered when the mouse is released.
     */
    private final EventHandler<MouseEvent> mouseReleased = e -> {

        if (arrowing != null) {

            final Square start = getSquareByPoint(arrowing.getSceneX(), arrowing.getSceneY(), true);
            final Square end = getSquareByPoint(e.getSceneX(), e.getSceneY(), true);

            if (start.isValid() && end.isValid()) {

                int color = 2;
                if (e.isShortcutDown()) {
                    color = 2;
                } else if (e.isAltDown()) {
                    color = 3;
                } else if (e.isShiftDown()) {
                    color = 4;
                } else
                    color = 5;

                arrowPane.arrow(start, end, color);
                arrowPane.redraw();
            }

            arrowing = null;
            return;

        } else if (e.getButton() == MouseButton.SECONDARY) {

            final Square targetSquare = getSquareByPoint(e.getSceneX(), e.getSceneY(), true);

            if (e.isShortcutDown()) {
                highlightPane.highlight(targetSquare, 2);
            } else if (e.isAltDown()) {
                highlightPane.highlight(targetSquare, 3);
            } else if (e.isShiftDown()) {
                highlightPane.highlight(targetSquare, 4);
            } else
                highlightPane.highlight(targetSquare, 5);

            highlightPane.redraw();

            return;

        }

        if (e.getButton() != MouseButton.PRIMARY)
            return;

        if (resizing != null) {

            resizing = null;

            try {

                highlightPane.setVisible(true);
                arrowPane.setVisible(true);
                borderPane.setVisible(true);
                moveIndicatorsPane.setVisible(true);
                piecePane.setVisible(true);
                piecePane.initPieceTranscoders();
                gameView.getGameInfoPane().initPieceTranscoders();
                draw();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return;
        }

        if (gameView.getGame() == null || gameView.getGame().isPaused())
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

    /**
     * Creates a new board.
     * 
     * @param gameView The gameView that houses this board.
     */
    public Board(GameView gameView) {

        this.gameView = gameView;

        squarePane = new Squares(gameView);
        highlightPane = new Highlights(gameView);
        coordsPane = new Coordinates(gameView);
        moveIndicatorsPane = new MoveIndicators(gameView);
        borderPane = new SquareBorders(gameView);
        piecePane = new Pieces(gameView);
        arrowPane = new Arrows(gameView);
        pausePane = new PauseView();

        pausePane.setVisible(false);

        getChildren().addAll(squarePane,
                highlightPane,
                coordsPane,
                moveIndicatorsPane,
                borderPane,
                piecePane,
                arrowPane,
                pausePane);

        gameView.getApp().getStage().addEventHandler(WindowEvent.WINDOW_SHOWN,
                we -> {

                    try {

                        squarePane.draw();
                        coordsPane.draw();

                        piecePane.initPieceTranscoders();
                        gameView.getGameInfoPane().initPieceTranscoders();

                    } catch (Exception e) {
                    }

                });

    }

    /**
     * Gets the GameView that contains this board.
     * 
     * @return The GameView that contains this board.
     */
    public GameView getGameView() {
        return gameView;
    }

    /**
     * Gets the square pane.
     * 
     * @return {@link #squarePane}
     */
    public Squares getSquarePane() {
        return squarePane;
    }

    /**
     * Gets the highlight pane.
     * 
     * @return {@link #highlightPane}
     */
    public Highlights getHighlightPane() {
        return highlightPane;
    }

    /**
     * Gets the border pane.
     * 
     * @return {@link #borderPane}
     */
    public SquareBorders getBorderPane() {
        return borderPane;
    }

    /**
     * Gets the move indicators pane.
     * 
     * @return {@link #moveIndicatorsPane}
     */
    public MoveIndicators getMoveIndicatorsPane() {
        return moveIndicatorsPane;
    }

    /**
     * Gets the piece pane.
     * 
     * @return {@link #piecePane}
     */
    public Pieces getPiecePane() {
        return piecePane;
    }

    /**
     * Gets the pause pane.
     * 
     * @return {@link #pausePane}
     */
    public PauseView getPausePane() {
        return pausePane;
    }

    /**
     * Gets the height and width of the squares, in pixels.
     * 
     * @return {@link #squareSize}
     */
    public double getSquareSize() {
        return squareSize;
    }

    /**
     * Gets the height and width of the pieces, in pixels.
     * 
     * @return {@link #pieceSize}
     */
    public double getPieceSize() {
        return pieceSize;
    }

    /**
     * Gets the currently active piece.
     * 
     * @return {@link #active}
     */
    public GUIPiece getActive() {
        return active;
    }

    /**
     * Sets the currently active piece.
     * 
     * @param active The currently active piece. May be {@code null} if the active
     *               piece is to be cleared.
     */
    public void setActive(GUIPiece active) {

        if (this.active != null && (active == null || !this.active.getPiece().equals(active.getPiece())))
            this.active.setAlreadyActive(false);

        this.active = active;

        activeUpdated();
    }

    /**
     * Gets the piece that is currently being dragged.
     * 
     * @return {@link #dragging}
     */
    public GUIPiece getDragging() {
        return dragging;
    }

    /**
     * Sets the piece that is currently being dragged.
     * 
     * @param dragging The piece currently being dragged. May be {@code null} if the
     *                 active piece is to be cleared.
     */
    public void setDragging(GUIPiece dragging) {
        this.dragging = dragging;
    }

    /**
     * Gets the layout bounds of the board in its scene.
     * 
     * @return {@link #boardBounds}
     */
    public Bounds getBoardBounds() {
        return boardBounds;
    }

    /**
     * Sets the layout bounds of the board in its scene.
     * 
     * @param boardBounds The new bounds.
     */
    public void setBoardBounds(Bounds boardBounds) {
        this.boardBounds = boardBounds;
    }

    /**
     * Gets the resize event.
     * 
     * @return {@link #resizeEvent}
     */
    public ChangeListener<Number> getResizeEvent() {
        return resizeEvent;
    }

    /**
     * Gets the mouse moved event.
     * 
     * @return {@link #mouseMoved}
     */
    public EventHandler<MouseEvent> getMouseMoved() {
        return mouseMoved;
    }

    /**
     * Gets the mouse released event.
     * 
     * @return {@link #mouseReleased}
     */
    public EventHandler<MouseEvent> getMouseReleased() {
        return mouseReleased;
    }

    /**
     * Gets the mouse pressed event.
     * 
     * @return {@link #mousePressed}
     */
    public EventHandler<MouseEvent> getMousePressed() {
        return mousePressed;
    }

    /**
     * Gets the mouse dragged event.
     * 
     * @return {@link #mouseDragged}
     */
    public EventHandler<MouseEvent> getMouseDragged() {
        return mouseDragged;
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

        resizeEvent.changed(null, null, null);

        if (gameView.getGame() == null) {

            borderPane.drawBorder(null);
            activeUpdated();

            gameView.getGameMenu().update();
            gameView.getViewMenu().update();

            gameView.getGameInfoPane().updateTimers();

            gameView.getMoveListPane().initMoveList();

            gameView.getGameInfoPane().getTopName().setText("");
            gameView.getGameInfoPane().getBottomName().setText("");

            setActive(null);
            setDragging(null);

            piecePane.draw();

            gameView.getOpeningLabelPane().update();

            return;

        }

        boolean ani = animate && dragging == null;
        setActive(null);
        setDragging(null);

        // Don't animate when promotion move (if you did it/two player game)
        if (p2 != null && p2.getMove() != null
                && p2.getMove().getPromoteType() != '0'
                && (getGUIPieceAtSquare(p2.getMove().getOrigin()) != null
                        && (getGUIPieceAtSquare(p2.getMove().getOrigin()).getPromoteMove() != null)))
            ani = false;

        piecePane.draw(backward, ani ? p1 : null, p2);

        gameView.getGameInfoPane().getTopTimer().setWhite(gameView.isFlipped());
        gameView.getGameInfoPane().getTopTimerHistory().setWhite(gameView.isFlipped());

        gameView.getGameInfoPane().getBottomTimer().setWhite(!gameView.isFlipped());
        gameView.getGameInfoPane().getBottomTimerHistory().setWhite(!gameView.isFlipped());

        gameView.getGameInfoPane().getTopCap().setWhite(gameView.isFlipped());
        gameView.getGameInfoPane().getBottomCap().setWhite(!gameView.isFlipped());

        gameView.getGameInfoPane().updateTimers();

        gameView.getGameInfoPane().getTopName().setText(gameView.getGame().getPlayer(gameView.isFlipped()).getName());
        gameView.getGameInfoPane().getBottomName()
                .setText(gameView.getGame().getPlayer(!gameView.isFlipped()).getName());

        borderPane.drawBorder(null);

        gameView.getOpeningLabelPane().update();

        activeUpdated();

        coordsPane.draw();

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

        Promote pD = new Promote(gameView, white, getScene().getWindow());

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

        } else if (moveIndicatorsPane.getMoveSquares().contains(getSquareByPoint(mouseX, mouseY, true))) {

            setCursor(Cursor.HAND);

        } else if (getSquareByPoint(mouseX, mouseY, true).isValid()
                && gameView.getGame().getPositions().get(gameView.getCurrentPos())
                        .getPieceAtSquare(getSquareByPoint(mouseX, mouseY, true)) != null) {

            setCursor(Cursor.OPEN_HAND);

        } else {
            setCursor(Cursor.DEFAULT);
        }

    }

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
                && y >= boardBounds.getMinY() /* - resizeSize */ && y <= boardBounds.getMaxY() /* + resizeSize */);

    }

    /**
     * Gets the {@link Square} that {@code x} and {@code y} fall within.
     * 
     * <p>
     * If {@code relative} is {@code true}, the {@code x} and {@code y} values will
     * be treated as coming from the outer VBox that encases the
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
     * the outer VBox that encases the {@link GameView}.
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
     * the outer VBox that encases the {@link GameView}.
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

    /**
     * Updates the square highlights and moves indicator panes.
     */
    private void activeUpdated() {

        highlightPane.draw();
        arrowPane.draw();
        moveIndicatorsPane.draw();

    }

}
