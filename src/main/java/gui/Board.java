package gui;

import game.GameSettings;
import game.Game;
import game.Move;
import game.Player;
import game.GameEvent;
import game.GameListener;
import game.Position;
import game.Square;
import game.LAN.Client;
import game.pieces.Piece;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Board extends VBox implements GameListener {

    private static final Color SQUARE_DARK = Color.rgb(155, 182, 124, 1);
    private static final Color SQUARE_LIGHT = Color.rgb(245, 241, 218, 1);
    private static final Color SQUARE_ACTIVE = Color.rgb(238, 187, 77, .70);
    private static final Color SQUARE_PREV_MOVE = Color.rgb(238, 187, 85, .70);
    private static final Color SQUARE_BORDER = Color.rgb(200, 200, 200, .5);
    private static final Color ATTACK_INDICATOR_COLOR = Color.rgb(100, 100, 100, .4);

    public static final int TWO_PLAYER = 0;
    public static final int WHITE = 1;
    public static final int BLACK = 2;

    private int squareSize = 100;
    private int pieceSize = 90;

    private Game game;
    private Client client;

    private ArrayList<GUIPiece> pieces;
    private ArrayList<PieceTranscoder> transcoderPieces;

    private GUIPiece active;
    private GUIPiece dragging;

    private boolean flipped;
    private int color;

    private int currentPos;

    private StackPane stack;
    private GUITimer topTimer;
    private GUITimer bottomTimer;

    private VBox squarePane;
    private Canvas squareHighlightPane;
    private Canvas borderPane;
    private Pane piecePane;

    private Canvas movesPane;

    private MovePane movePane;
    private ScrollPane scrollMovePane;

    private BarMenu menuBar;
    private GameMenu gameMenu;
    private ViewMenu viewMenu;

    private DrawDialog drawDialog;

    private ArrayList<TranslateTransition> transitions;

    public ArrayList<TranslateTransition> getTransitions() {
        return transitions;
    }

    private EventHandler<KeyEvent> keyHandler = ev -> {

        if (game == null)
            return;

        if (ev.getCode() == (KeyCode.LEFT)) {

            decPos();

        } else if (ev.getCode() == (KeyCode.RIGHT)) {

            incPos();

        } else if (ev.getCode() == KeyCode.DOWN) {

            goToLastPos();

        } else if (ev.getCode() == KeyCode.UP) {

            goToFirstPos();

        } else if (ev.getCode() == KeyCode.ESCAPE) {

            clearSelection();

        }

    };

    private EventHandler<MouseEvent> mouseMoved = ev -> {

        setMouseType(ev.getSceneX(), ev.getSceneY());

    };

    private EventHandler<MouseEvent> mouseReleased = e -> {

        if (game == null)
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
                updateActive();
                clearBorder();

            }

        }

        setMouseType(e.getSceneX(), e.getSceneY());

    };

    private EventHandler<MouseEvent> mousePressed = ev -> {

        if (game == null)
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

        if (ev.getButton() != MouseButton.PRIMARY)
            return;

        if (dragging != null) {

            dragging.onMouseDragged(ev);

        }

        setMouseType(ev.getSceneX(), ev.getSceneY());

    };

    // Getters/Setters

    public int getCurrentPos() {
        return currentPos;
    }

    public Pane getPiecePane() {
        return piecePane;
    }

    public int getSquareSize() {
        return squareSize;
    }

    public int getPieceSize() {
        return pieceSize;
    }

    public Game getGame() {
        return game;
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

    public boolean isFlipped() {
        return flipped;
    }

    public int getColor() {
        return color;
    }

    public StackPane getStack() {
        return stack;
    }

    public MovePane getMovePane() {
        return movePane;
    }

    public ScrollPane getScrollMovePane() {
        return scrollMovePane;
    }

    public BarMenu getMenuBar() {
        return menuBar;
    }

    public EventHandler<KeyEvent> getKeyHandler() {
        return keyHandler;
    }

    public boolean isTurn() {

        if (color == TWO_PLAYER)
            return true;

        if (color == BLACK && !game.getLastPos().isWhite())
            return true;
        if (color == WHITE && game.getLastPos().isWhite())
            return true;

        return false;

    }

    public Board(int squareSize, BarMenu menuBar) throws Exception {

        // this.game = new Game();
        // game.addMoveListener(this);

        this.squareSize = squareSize;

        this.menuBar = menuBar;
        initMenus();

        this.color = 0;
        this.flipped = false;

        this.transitions = new ArrayList<TranslateTransition>();

        this.topTimer = new GUITimer(this, !flipped);
        this.bottomTimer = new GUITimer(this, flipped);

        HBox bottomTimerBox = new HBox(topTimer);
        HBox topTimerBox = new HBox(bottomTimer);
        bottomTimerBox.setAlignment(Pos.CENTER_RIGHT);
        topTimerBox.setAlignment(Pos.CENTER_RIGHT);

        stack = new StackPane();
        stack.setMaxWidth(squareSize * 8);
        stack.setMaxHeight(squareSize * 8);

        this.scrollMovePane = new ScrollPane();
        scrollMovePane.setFitToWidth(true);
        scrollMovePane.setMinWidth(220);

        this.movePane = new MovePane(this, scrollMovePane);
        scrollMovePane.setContent(movePane);

        this.squarePane = new VBox();
        squareHighlightPane = new Canvas(squareSize * 8, squareSize * 8);
        borderPane = new Canvas(squareSize * 8, squareSize * 8);
        movesPane = new Canvas(squareSize * 8, squareSize * 8);
        piecePane = new Pane();

        initSquares();
        initPieceTranscoders();

        stack.getChildren().addAll(squarePane, squareHighlightPane, borderPane, movesPane, piecePane);

        topTimerBox.setViewOrder(1);
        bottomTimerBox.setViewOrder(1);
        stack.setViewOrder(0);

        getChildren().addAll(topTimerBox, stack, bottomTimerBox);

        setOnMouseMoved(mouseMoved);
        setOnMousePressed(mousePressed);
        setOnMouseDragged(mouseDragged);
        setOnMouseReleased(mouseReleased);

    }

    public void setCurrentPos(int pos) {

        int old = currentPos;

        currentPos = pos;

        boardUpdated(Math.abs(pos - old) == 1, game.getPositions().get(old), game.getPositions().get(currentPos),
                old > currentPos);

        movePane.posChanged(currentPos);
        gameMenu.update();

    }

    // Actions
    void incPos() {

        if (currentPos + 1 < game.getPositions().size()) {

            setCurrentPos(currentPos + 1);

        }

    }

    void decPos() {

        if (currentPos - 1 >= 0) {

            setCurrentPos(currentPos - 1);
        }

    }

    void goToFirstPos() {

        setCurrentPos(0);

    }

    void goToLastPos() {

        setCurrentPos(game.getPositions().size() - 1);

    }

    void clearSelection() {

        active = null;
        dragging = null;
        updateActive();
        clearBorder();
        boardUpdated();

    }

    void flipBoard() {

        flipped = !flipped;
        boardUpdated();

    }

    void startGame(WindowEvent we) {

        GameSettingsDialog settings = new GameSettingsDialog(getScene().getWindow(), game);
        settings.setOnHidden(e -> {

            if (settings.isCreate() && settings.getTimePerSide() > -1) {

                if (game != null)
                    game.markGameOver(Game.RESULT_TERMINATED, Game.REASON_OTHER);

                if (settings.getClient() == null) {
                    try {

                        color = TWO_PLAYER;

                        game = new Game("White", "Black",
                                new GameSettings(settings.getTimePerSide(), settings.getTimePerMove(), true, true, true,
                                        true));

                        game.addListener(this);

                        game.startGame();
                        boardUpdated();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } else {

                    game = settings.getClient().getGame();
                    game.addListener(this);
                    color = settings.getClient().isOppColor() ? BLACK : WHITE;
                    client = settings.getClient();
                    if (color == BLACK)
                        flipBoard();

                    boardUpdated();

                }

                // game.addMoveListener(this);
                // game.addMoveListener(getMovePane());
                // game.addMoveListener(gameMenu);

                movePane.initMovePane();

            }

        });

        settings.showAndWait();

    }

    /**
     * Gets the corresponding {@link PieceTranscoder} for the type and color of the
     * piece given.
     * 
     * @param piece The piece to get the {@link PieceTranscoder} for
     * @return The {@link PieceTranscoder}
     */
    private PieceTranscoder getPieceTranscoder(Piece piece) {

        PieceTranscoder found = null;

        for (int i = 0; i < transcoderPieces.size() && found == null; i++) {

            PieceTranscoder pt = transcoderPieces.get(i);
            if (pt.isColor() == piece.isWhite() && pt.getPieceCode() == piece.getCode())
                found = pt;

        }

        return found;

    }

    // Drawing
    void clearBorder() {

        GraphicsContext gc = borderPane.getGraphicsContext2D();

        gc.clearRect(0.0, 0.0, borderPane.getLayoutBounds().getWidth(),
                borderPane.getLayoutBounds().getHeight());

    }

    void drawBorder(double x, double y) {

        clearBorder();

        GraphicsContext gc = borderPane.getGraphicsContext2D();

        double strokeWidth = squareSize / 20.0;

        gc.setLineWidth(strokeWidth);
        gc.setStroke(SQUARE_BORDER);

        gc.strokeRect(x + (strokeWidth / 2.0), y + (strokeWidth / 2.0), squareSize - strokeWidth,
                squareSize - strokeWidth);

    }

    private void drawMovesPane() {

        GraphicsContext gc = movesPane.getGraphicsContext2D();

        gc.clearRect(0.0, 0.0, movesPane.getLayoutBounds().getWidth(),
                movesPane.getLayoutBounds().getHeight());

        if (!isTurn() || active == null || game.getResult() != Game.RESULT_IN_PROGRESS)
            return;

        ArrayList<Move> pMoves = game.getLastPos().getMoves();

        gc.setFill(ATTACK_INDICATOR_COLOR);
        gc.setStroke(ATTACK_INDICATOR_COLOR);
        gc.setLineWidth(squareSize * 0.04);

        for (Move m : pMoves) {

            if (!m.getPiece().equals(active.getPiece()))
                continue;

            int x = getXBySquare(m.getDestination(), false);
            int y = getYBySquare(m.getDestination(), false);

            if (m.isCapture() && m.getCaptureSquare().equals(m.getDestination())) {

                gc.strokeOval(x + (squareSize * 0.05),
                        y + (squareSize * 0.05),
                        squareSize - (squareSize * .1),
                        squareSize - (squareSize * .1));

            } else
                gc.fillOval(x + (squareSize / 3.0),
                        y + (squareSize / 3.0),
                        squareSize / 3.0,
                        squareSize / 3.0);

        }

    }

    private void drawHighlightSq() {

        GraphicsContext gc = squareHighlightPane.getGraphicsContext2D();

        gc.clearRect(0.0, 0.0, squareHighlightPane.getLayoutBounds().getWidth(),
                squareHighlightPane.getLayoutBounds().getHeight());

        gc.setFill(SQUARE_PREV_MOVE);

        if (currentPos > 0) {

            Position pos = game.getPositions().get(currentPos);

            Square origin = pos.getMove().getOrigin();
            gc.fillRect(getXBySquare(origin), getYBySquare(origin), squareSize, squareSize);

            Square destination = pos.getMove().getDestination();
            gc.fillRect(getXBySquare(destination), getYBySquare(destination), squareSize, squareSize);

        }

        gc.setFill(SQUARE_ACTIVE);

        if (active != null) {

            gc.fillRect(getXBySquare(active.getPiece().getSquare()),
                    getYBySquare(active.getPiece().getSquare()),
                    squareSize,
                    squareSize);

        } else if (dragging != null) {

            gc.fillRect(getXBySquare(dragging.getPiece().getSquare()),
                    getYBySquare(dragging.getPiece().getSquare()),
                    squareSize,
                    squareSize);

        }

    }

    private void setMouseType(double mouseX, double mouseY) {

        if (game == null) {
            setCursor(Cursor.DEFAULT);
            return;
        }

        if (dragging != null) {

            setCursor(Cursor.CLOSED_HAND);

        } else if (getSquareByLoc(mouseX, mouseY, true).isValid()
                && game.getPositions().get(currentPos)
                        .getPieceAtSquare(getSquareByLoc(mouseX, mouseY, true)) != null) {

            setCursor(Cursor.OPEN_HAND);

        } else {
            setCursor(Cursor.DEFAULT);
        }

    }

    /**
     * Creates and plays an animation of a piece moving. If {@code capture} is not
     * {@code null}, the capture
     * piece will still show up until the animation completes.
     * 
     * @param guiPiece    The piece to animate
     * @param origin      The start square of the animated piece
     * @param destination The end square of the animated piece
     * @param capture     The piece captured by {@code guiPiece}. Should be
     *                    {@code null} if
     *                    there is no piece being captured.
     */
    void pieceMoveAnimation(GUIPiece guiPiece, Square origin, Square destination, Piece capture) {
        pieceMoveAnimation(guiPiece, origin, destination, capture, null);
    }

    /**
     * Creates and plays an animation of a piece moving. If {@code capture} is not
     * {@code null}, the capture
     * piece will still show up until the animation completes.
     * 
     * @param guiPiece    The piece to animate
     * @param origin      The start square of the animated piece
     * @param destination The end square of the animated piece
     * @param capture     The piece captured by {@code guiPiece}. Should be
     *                    {@code null} if
     *                    there is no piece being captured.
     */
    void pieceMoveAnimation(GUIPiece guiPiece, Square origin, Square destination, Piece capture, Runnable callback) {

        ImageView img = guiPiece.getImage();

        TranslateTransition t = new TranslateTransition(Duration.millis(100), img);

        double fromX = getXBySquare(origin) + ((squareSize - pieceSize) / 2.0);
        double fromY = getYBySquare(origin) + ((squareSize - pieceSize) / 2.0);

        double toX = getXBySquare(destination) + ((squareSize - pieceSize) / 2.0);
        double toY = getYBySquare(destination) + ((squareSize - pieceSize) / 2.0);

        t.setFromX(fromX - toX);
        t.setFromY(fromY - toY);

        t.setToX(0);
        t.setToY(0);

        if (capture != null) {

            ImageView i = getPieceTranscoder(capture).getImageView();

            piecePane.getChildren().add(i);

            GUIPiece guiP = new GUIPiece(capture, i, this);

            i.setLayoutX(getXBySquare(capture.getSquare()) + ((squareSize - pieceSize) / 2.0));
            i.setLayoutY(((getYBySquare(capture.getSquare()))) + ((squareSize - pieceSize) / 2.0));

            t.getNode().toFront();

            Runnable cb = callback;
            t.setOnFinished(e -> {

                piecePane.getChildren().remove(guiP.getImage());

                if (cb != null)
                    cb.run();

            });

        } else {
            Runnable cb = callback;
            t.setOnFinished(e -> {

                if (cb != null)
                    cb.run();

            });

        }

        transitions.add(t);
    }

    /**
     * Clears the old pieces and draws the pieces on the board.
     * 
     * @param animate  Whether or not the move should be animated. Must be
     *                 {@code false} if the positions are not back to back.
     * @param pos1     The position before the board was updated.
     * @param pos2     The position after the board was updated. If {@code null},
     *                 the active position of the game will be used instead.
     * @param backward {@code true} if {@code p2} is before {@code p1} (such as when
     *                 undoing.)
     */
    private void drawPieces(boolean animate, Position pos1, Position pos2, boolean backward) {

        this.pieces = new ArrayList<GUIPiece>();
        transitions = new ArrayList<TranslateTransition>();

        piecePane.getChildren().clear();

        if (pos2 == null)
            pos2 = game.getPositions().get(currentPos);

        for (int r = 0; r < 8; r++) {

            for (int c = 0; c < 8; c++) {

                Piece p = pos2.getPieceAtSquare(new Square(r + 1, c + 1));

                if (p == null)
                    continue;

                ImageView img = getPieceTranscoder(p).getImageView();
                GUIPiece guiP = new GUIPiece(p, img, this);

                piecePane.getChildren().add(img);

                pieces.add(guiP);

                img.setLayoutX(getXBySquare(p.getSquare()) + ((squareSize - pieceSize) / 2.0));
                img.setLayoutY(getYBySquare(p.getSquare()) + ((squareSize - pieceSize) / 2.0));

                if (animate && pos1 != null && pos2 != null
                        && ((!backward && pos2.getMove() != null) || (backward && pos1.getMove() != null))
                        // Either not backwards and the piece in the move of p2 is this piece
                        && ((!backward && pos2.getMove().getDestination().equals(p.getSquare()))
                                // Or it is backwards and the piece in the move of p1 is this piece
                                || (backward && pos1.getMove().getOrigin().equals(p.getSquare()))
                                // Same as first term, but for castle moves.
                                || (!backward && pos2.getMove().isCastle()
                                        && pos2.getMove().getRookDestination().equals(p.getSquare()))
                                // Same as second term, but for castle moves.
                                || (backward && pos1.getMove().isCastle()
                                        && pos1.getMove().getRookOrigin().equals(p.getSquare())))) {

                    if (!backward) {

                        if (pos2.getMove().isCastle() && pos2.getMove().getRookDestination().equals(p.getSquare())) {

                            pieceMoveAnimation(guiP, pos2.getMove().getRookOrigin(),
                                    pos2.getMove().getRookDestination(),
                                    null);

                        } else
                            pieceMoveAnimation(guiP, pos2.getMove().getOrigin(), pos2.getMove().getDestination(),
                                    pos2.getMove().getCapturePiece());

                    } else {

                        if (pos1.getMove().isCastle() && pos1.getMove().getRookOrigin().equals(p.getSquare())) {
                            pieceMoveAnimation(guiP, pos1.getMove().getRookDestination(),
                                    pos1.getMove().getRookOrigin(),
                                    null);
                        } else
                            pieceMoveAnimation(guiP, pos1.getMove().getDestination(), pos1.getMove().getOrigin(),
                                    pos1.getMove().getCapturePiece());

                    }

                }

            }
        }

        for (TranslateTransition t : transitions) {
            t.play();
        }

    }

    /**
     * Updates the square highlights and moves panes.
     */
    void updateActive() {

        drawHighlightSq();
        drawMovesPane();

    }

    void updateTimers() {

        topTimer.update();
        bottomTimer.update();

    }

    void boardUpdated() {
        boardUpdated(false, null, null, false);
    }

    void boardUpdated(boolean animate, Position p1, Position p2, boolean backward) {

        updateActive();
        viewMenu.update();

        boolean ani = animate && dragging == null;
        dragging = null;

        if (game.getLastPos().getMove() != null && game.getLastPos().getMove().getPromoteType() != '0'
                && (color == TWO_PLAYER || game.getLastPos().isWhite() != (color == WHITE)))
            ani = false;

        drawPieces(ani, p1, p2, backward);

        topTimer.setWhite(!flipped);
        topTimer.update();

        bottomTimer.setWhite(flipped);
        bottomTimer.update();

        gameMenu.update();
        drawMovesPane();

        // Position activePos = game.getPositions().get(currentPos);
        // if (activePos.getMove() != null && activePos.getMove().getPromoteType() ==
        // '?') {

        // try {

        // showPromoteDialog();

        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        // }

    }

    public void showPromoteDialog(Square square, boolean white, GUIPiece callback) throws Exception {

        PromoteDialog pD = new PromoteDialog(pieceSize, squareSize, white, flipped,
                getScene().getWindow());

        pD.setOnHidden(ev -> {

            callback.setPromoteResponse(pD.getResult());
            callback.getPromoteCallback().run();

        });

        Bounds bds = stack.localToScreen(stack.getBoundsInParent());

        pD.setX(bds.getMinX() + getXBySquare(square, true));
        pD.setY(bds.getMinY() + getYBySquare(square, true)
                - ((!white && flipped) || (white && !flipped)
                        ? (-squareSize)
                        : (squareSize) * (4 + (1 / 3.0))));

        pD.show();
        pD.sizeToScene();

    }

    public GUIPiece getGUIPieceAtSquare(Square square) {

        GUIPiece found = null;

        for (int i = 0; i < pieces.size() && found == null; i++) {

            if (pieces.get(i).getPiece().getSquare().equals(square))
                found = pieces.get(i);

        }

        return found;

    }

    // Calculations

    /**
     * Gets the {@link Square} that {@code x} and {@code y} fall within.
     * 
     * <p>
     * If {@code relative} is {@code true}, the {@code x} and {@code y} values will
     * be treated as coming from the outer {@link VBox} that encases the
     * {@link Board}.
     * 
     * @param x        The x location
     * @param y        The y location
     * @param relative Whether or not to treat the {@code x} and {@code y} values as
     *                 relative values
     * @return The {@link Square} object
     */
    public Square getSquareByLoc(double x, double y, boolean relative) {

        if (relative) {
            Bounds bds = stack.localToScene(getBoundsInParent());
            x -= bds.getMinX();
            y -= bds.getMinY();
        }

        if (x < 0 || x > squareSize * 8 || y < 0 || y > squareSize * 8)
            return new Square(-1, -1);

        if (!flipped)
            return new Square(((((int) x) / squareSize) + 1),
                    8 - ((int) y / squareSize));
        else
            return new Square(8 - (((int) x) / squareSize), (int) y / squareSize + 1);

    }

    /**
     * Gets the x location of the provided {@link Square}.
     * 
     * @param square The {@link Square} to get the x value for
     * @return The x location of the top left of the {@link Square}
     */
    public int getXBySquare(Square square) {

        return getXBySquare(square, false);

    }

    /**
     * Gets the x location of the provided {@link Square}.
     * 
     * <p>
     * If {@code relative} is {@code true}, the x value will be in the context of
     * the outer {@link VBox} that encases the {@link Board}.
     * 
     * @param square   The {@link Square} to get the x value for
     * @param relative Whether or not to treat the x value as relative
     * @return The x location of the top left of the {@link Square}
     */
    public int getXBySquare(Square square, boolean relative) {

        int rel = 0;

        if (relative) {

            Bounds bds = stack.localToScene(getBoundsInParent());

            rel = (int) bds.getMinX();

        }

        if (!flipped)
            return (int) ((square.getFile() - 1) * squareSize) - rel;
        else
            return (int) ((squareSize * 7) - ((square.getFile() - 1) * squareSize)) - rel;

    }

    /**
     * Gets the y location of the provided {@link Square}.
     * 
     * @param square The {@link Square} to get the y value for
     * @return The y location of the top left of the {@link Square}
     */
    public int getYBySquare(Square square) {

        return getYBySquare(square, false);

    }

    /**
     * Gets the y location of the provided {@link Square}.
     * 
     * <p>
     * If {@code relative} is {@code true}, the y value will be in the context of
     * the outer {@link VBox} that encases the {@link Board}.
     * 
     * @param square   The {@link Square} to get the y value for
     * @param relative Whether or not to treat the y value as relative
     * @return The y location of the top left of the {@link Square}
     */
    public int getYBySquare(Square square, boolean relative) {

        int rel = 0;

        if (relative) {

            Bounds bds = stack.localToScene(getBoundsInParent());

            rel = (int) bds.getMinY();

        }

        if (!flipped)
            return (int) ((squareSize * 7) - ((square.getRank() - 1) * squareSize)) - rel;
        else
            return (int) ((square.getRank() - 1) * squareSize) - rel;

    }

    // Initializers
    private void initPieceTranscoders() throws Exception {

        transcoderPieces = new ArrayList<PieceTranscoder>();

        boolean color = true;

        for (int i = 0; i < 2; i++) {

            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'K'));
            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'Q'));
            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'R'));
            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'B'));
            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'N'));
            transcoderPieces.add(new PieceTranscoder(pieceSize, color, 'P'));

            color = false;

        }

    }

    private void initMenus() {

        viewMenu = new ViewMenu(this);
        gameMenu = new GameMenu(this);

        menuBar.getMenus().addAll(gameMenu, viewMenu, new Menu("Help"));

    }

    private void initSquares() {

        boolean dark = false;

        for (int r = 0; r < 8; r++) {

            HBox hbox = new HBox();

            for (int c = 0; c < 8; c++, dark = !dark) {

                Rectangle sq = new Rectangle(squareSize, squareSize, dark ? SQUARE_DARK : SQUARE_LIGHT);

                StackPane pane = new StackPane(sq);

                hbox.getChildren().add(pane);

            }

            dark = !dark;

            squarePane.getChildren().add(hbox);
        }

    }

    // Event Handlers

    @Override
    public void onPlayerEvent(GameEvent event) {

        Platform.runLater(() -> {

            if (event.getType() == GameEvent.TYPE_MOVE) {

                currentPos = event.getCurrIndex();
                movePane.posChanged(currentPos);

                // if (color == TWO_PLAYER)
                // flipBoard();

                boardUpdated(true, event.getPrev(), event.getCurr(), event.getPrevIndex() > event.getCurrIndex());

                movePane.boardUpdated();
                gameMenu.update();
                viewMenu.update();

            } else if (event.getType() == GameEvent.TYPE_DRAW_OFFER) {

                if ((color == WHITE || color == BLACK) && game.getLastPos().getDrawOfferer() == color)
                    return;

                drawDialog = new DrawDialog(this, game.getPlayer(client.isOppColor()).getName());

                drawDialog.setOnHidden(ev -> {

                    if (drawDialog.isAccept()) {

                        try {
                            game.acceptDrawOffer();
                        } catch (Exception e) {

                        }

                    }

                });

                drawDialog.show();

            } else if (event.getType() == GameEvent.TYPE_OVER) {

                if (game.getResult() <= Game.RESULT_IN_PROGRESS || game.getResult() == Game.RESULT_TERMINATED)
                    return;

                Dialog<Void> over = new Dialog<Void>();
                over.setTitle("Game Over");

                String msg = "";
                if (game.getResult() == Game.RESULT_DRAW) {
                    msg = "Draw";
                } else if (game.getResult() == Game.RESULT_BLACK_WIN) {
                    msg = "Black win";
                } else if (game.getResult() == Game.RESULT_WHITE_WIN) {
                    msg = "White win";
                }

                String reason = "";

                switch (game.getResultReason()) {

                    case Game.REASON_CHECKMATE:
                        reason = " by checkmate.";
                        break;
                    case Game.REASON_FLAGFALL:
                        reason = " by flagfall.";
                        break;
                    case Game.REASON_DEAD_INSUFFICIENT_MATERIAL:
                        reason = " due to insufficient material.";
                        break;
                    case Game.REASON_DEAD_NO_POSSIBLE_MATE:
                        reason = " due to no possible checkmate.";
                        break;
                    case Game.REASON_FIFTY_MOVE:
                        reason = " by fifty move rule.";
                        break;
                    case Game.REASON_REPETITION:
                        reason = " by repetition.";
                        break;
                    case Game.REASON_STALEMATE:
                        reason = " by stalemate.";
                        break;
                    case Game.REASON_RESIGNATION:
                        reason = " by resignation.";
                        break;
                    default:
                        reason = ".";
                        break;

                }

                msg += reason;

                over.setContentText(msg);

                over.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

                over.showAndWait();

            }

        });

    }

}
