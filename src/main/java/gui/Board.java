package gui;

import game.GameSettings;
import game.Game;
import game.Move;
import game.GameEvent;
import game.GameListener;
import game.Position;
import game.Square;
import game.LAN.Client;
import game.pieces.Piece;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Board extends HBox implements GameListener {

    public static final int TWO_PLAYER = 0;
    public static final int WHITE = 1;
    public static final int BLACK = 2;

    private double squareSize = 90;
    private double pieceSize = 80;

    private Game game;
    private Client client;

    private ArrayList<GUIPiece> pieces;
    private ArrayList<PieceTranscoder> transcoderPieces;

    private GUIPiece active;
    private GUIPiece dragging;

    private boolean flipped;
    private int color;

    private int currentPos;

    private App app;

    private StackPane stack;
    private Pane infoPane;

    private GUITimer topTimer;
    private GUITimer bottomTimer;

    private Label topName;
    private Label bottomName;

    private Pane squarePane;
    private Pane highlightPane;
    private Pane borderCanvas;
    private Pane movesCanvas;
    private Pane piecePane;

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

    public Pane getSquarePane() {
        return squarePane;
    }

    public void keyHandler(KeyEvent ev) {

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

    }

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

        if (game == null)
            return;

        if (ev.getButton() != MouseButton.PRIMARY)
            return;

        if (dragging != null) {

            dragging.onMouseDragged(ev);

        }

    };

    private Timer resizer = new Timer("Resizer", true);
    private TimerTask resizeTask = null;
    private ChangeListener<Number> resizeEvent = (obs, o, n) -> {

        if (resizeTask != null) {
            resizeTask.cancel();
        }

        resizeTask = new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    Bounds ip = infoPane.getBoundsInParent();
                    Bounds mp = scrollMovePane.getBoundsInParent();

                    double width = mp.getMinX() - ip.getMaxX();
                    double height = stack.getBoundsInParent().getHeight();
                    System.out.println(width + " " + height);
                    if (width < height) {
                        squareSize = width / 8;
                        pieceSize = squareSize * 0.9;
                    } else {
                        squareSize = height / 8;
                        pieceSize = squareSize * 0.9;
                    }

                    squarePane.setMinSize(squareSize * 8, squareSize * 8);
                    highlightPane.setMinSize(squareSize * 8, squareSize * 8);
                    borderCanvas.setMinSize(squareSize * 8, squareSize * 8);
                    movesCanvas.setMinSize(squareSize * 8, squareSize * 8);
                    piecePane.setMinSize(squareSize * 8, squareSize * 8);

                    try {
                        initSquares();
                        initPieceTranscoders();
                        boardUpdated();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        };

        resizer.schedule(resizeTask, 200);

    };

    // Getters/Setters

    public int getCurrentPos() {
        return currentPos;
    }

    public Pane getPiecePane() {
        return piecePane;
    }

    public double getSquareSize() {
        return squareSize;
    }

    public double getPieceSize() {
        return pieceSize;
    }

    public Game getGame() {
        return game;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public void setGame(Game game) {
        this.game = game;
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

    public Board(App app, BarMenu menuBar) throws Exception {

        setId("board");

        this.app = app;

        this.menuBar = menuBar;
        this.transitions = new ArrayList<TranslateTransition>();

        this.color = TWO_PLAYER;
        this.flipped = false;

        // Info Pane (timer & player names)

        // - Top timer
        topTimer = new GUITimer(this, flipped);
        topTimer.setAlignment(Pos.CENTER);

        topName = new Label();
        topName.setId("nameLabel");
        topName.setAlignment(Pos.CENTER);

        VBox topInfoBox = new VBox(topName, topTimer);
        topInfoBox.setId("infoBox");
        topInfoBox.setAlignment(Pos.TOP_CENTER);

        // - Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // - Bottom timer
        this.bottomTimer = new GUITimer(this, !flipped);

        bottomTimer.setAlignment(Pos.CENTER);

        bottomName = new Label();
        bottomName.setId("nameLabel");

        bottomName.setAlignment(Pos.CENTER);

        VBox bottomInfoBox = new VBox(bottomTimer, bottomName);
        bottomInfoBox.setId("infoBox");
        topInfoBox.setAlignment(Pos.BOTTOM_CENTER);

        infoPane = new VBox(topInfoBox, spacer, bottomInfoBox);
        infoPane.setId("infoPane");

        // Board view
        scrollMovePane = new ScrollPane();
        scrollMovePane.setFitToWidth(true);
        scrollMovePane.setFitToHeight(true);
        scrollMovePane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollMovePane.setVbarPolicy(ScrollBarPolicy.NEVER);
        scrollMovePane.setMinWidth(220);

        movePane = new MovePane(this, scrollMovePane);
        scrollMovePane.setContent(movePane);

        squarePane = new Pane();
        squarePane.setId("squarePane");

        highlightPane = new Pane();
        borderCanvas = new Pane();
        movesCanvas = new Pane();
        piecePane = new Pane();

        squarePane.setManaged(false);
        highlightPane.setManaged(false);
        movesCanvas.setManaged(false);
        piecePane.setManaged(false);

        stack = new StackPane(squarePane, highlightPane, borderCanvas, movesCanvas, piecePane);
        stack.setId("stack");

        app.getStage().widthProperty().addListener(resizeEvent);
        app.getStage().heightProperty().addListener(resizeEvent);

        // Initializers
        initMenus();
        initSquares();
        initPieceTranscoders();

        getChildren().addAll(infoPane, stack, scrollMovePane);

        scrollMovePane.setViewOrder(1);
        infoPane.setViewOrder(1);
        stack.setViewOrder(0);

        setOnMouseMoved(mouseMoved);
        setOnMousePressed(mousePressed);
        setOnMouseDragged(mouseDragged);
        setOnMouseReleased(mouseReleased);

        HBox.setHgrow(infoPane, Priority.NEVER);
        HBox.setHgrow(stack, Priority.ALWAYS);
        HBox.setHgrow(scrollMovePane, Priority.NEVER);

        HBox.setMargin(infoPane, new Insets(5, 5, 5, 5));
        HBox.setMargin(stack, new Insets(5, 5, 5, 5));
        HBox.setMargin(scrollMovePane, new Insets(5, 5, 5, 5));

        boardUpdated();

    }

    public void setPos(int pos) {
        this.currentPos = pos;
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

        final GameSettingsDialog settings = new GameSettingsDialog(getScene().getWindow(), this);

        settings.setOnHidden(e -> {

            if (settings.isCreate()) {

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

        borderCanvas.getChildren().clear();

    }

    void drawBorder(double x, double y) {

        clearBorder();

        double strokeWidth = squareSize / 20.0;

        Rectangle border = new Rectangle(squareSize - strokeWidth, squareSize - strokeWidth);
        border.setId("squareBorder");

        border.setLayoutX(x + (strokeWidth / 2.0));
        border.setLayoutY(y + (strokeWidth / 2.0));

        border.setStrokeWidth(strokeWidth);

        borderCanvas.getChildren().add(border);

    }

    private void drawMovesPane() {

        movesCanvas.getChildren().clear();

        if (game == null || !isTurn() || active == null || game.getResult() != Game.RESULT_IN_PROGRESS)
            return;

        ArrayList<Move> pMoves = game.getLastPos().getMoves();

        // gc.setFill(ATTACK_INDICATOR_COLOR);
        // gc.setStroke(ATTACK_INDICATOR_COLOR);
        // gc.setLineWidth(squareSize * 0.04);

        for (Move m : pMoves) {

            if (!m.getPiece().equals(active.getPiece()))
                continue;

            double x = getXBySquare(m.getDestination(), false);
            double y = getYBySquare(m.getDestination(), false);

            if (m.isCapture() && m.getCaptureSquare().equals(m.getDestination())) {

                Ellipse captureCircle = new Ellipse((squareSize - (squareSize * .1)) / 2.0,
                        (squareSize - (squareSize * .1)) / 2.0);
                captureCircle.setId("captureCircle");
                captureCircle.setLayoutX(x + (squareSize / 2.0));
                captureCircle.setLayoutY(y + (squareSize / 2.0));
                captureCircle.setStrokeWidth(squareSize * 0.04);

                movesCanvas.getChildren().add(captureCircle);

            } else {

                Ellipse nonCaptureCircle = new Ellipse(squareSize / 6.0, squareSize / 6.0);
                nonCaptureCircle.setId("nonCaptureCircle");
                nonCaptureCircle.setLayoutX(x + (squareSize / 2.0));
                nonCaptureCircle.setLayoutY(y + (squareSize / 2.0));

                movesCanvas.getChildren().add(nonCaptureCircle);

            }
        }

    }

    private void drawHighlightSq() {

        // GraphicsContext gc = highlightPane.getGraphicsContext2D();

        // gc.clearRect(0.0, 0.0, highlightPane.getLayoutBounds().getWidth(),
        // highlightPane.getLayoutBounds().getHeight());

        highlightPane.getChildren().clear();

        if (game == null)
            return;

        // gc.setFill(SQUARE_PREV_MOVE);

        if (currentPos > 0) {

            Position pos = game.getPositions().get(currentPos);

            Square origin = pos.getMove().getOrigin();
            Rectangle oRect = new Rectangle(0, 0, squareSize, squareSize);
            oRect.setLayoutX(getXBySquare(origin));
            oRect.setLayoutY(getYBySquare(origin));
            oRect.setId("moveSquare");

            Square destination = pos.getMove().getDestination();
            Rectangle dRect = new Rectangle(getXBySquare(destination), getYBySquare(destination), squareSize,
                    squareSize);
            dRect.setId("moveSquare");

            highlightPane.getChildren().addAll(oRect, dRect);

        }

        // gc.setFill(SQUARE_ACTIVE);

        if (active != null) {

            Rectangle aRect = new Rectangle(getXBySquare(active.getPiece().getSquare()),
                    getYBySquare(active.getPiece().getSquare()),
                    squareSize,
                    squareSize);
            aRect.setId("activeSquare");

            highlightPane.getChildren().addAll(aRect);

        } else if (dragging != null) {

            Rectangle aRect = new Rectangle(getXBySquare(dragging.getPiece().getSquare()),
                    getYBySquare(dragging.getPiece().getSquare()),
                    squareSize,
                    squareSize);
            aRect.setId("activeSquare");

            highlightPane.getChildren().addAll(aRect);

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

        TranslateTransition t = new TranslateTransition(Duration.millis(150), img);

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

        if (game == null)
            return;

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

        if (game == null) {

            clearBorder();
            drawMovesPane();
            drawHighlightSq();
            viewMenu.update();
            topTimer.update();
            bottomTimer.update();
            gameMenu.update();
            movePane.initMovePane();
            active = null;
            dragging = null;
            drawPieces(false, null, null, false);
            topName.setText("");
            bottomName.setText("");

            return;

        }

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

        topName.setText(game.getPlayer(flipped).getName());
        bottomName.setText(game.getPlayer(!flipped).getName());

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

        Bounds bds = piecePane.localToScreen(piecePane.getBoundsInLocal());

        pD.setX(bds.getMinX() + getXBySquare(square));
        pD.setY(bds.getMinY() + getYBySquare(square)
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
            Bounds bds = piecePane.localToScene(getBoundsInParent());
            x -= bds.getMinX();
            y -= bds.getMinY();
        }

        if (x < 0 || x > squareSize * 8 || y < 0 || y > squareSize * 8)
            return new Square(-1, -1);

        if (!flipped)
            return new Square((int) (x / squareSize) + 1, 8 - (int) (y / squareSize));
        else
            return new Square(8 - (int) (x / squareSize), (int) (y / squareSize) + 1);
        // if (!flipped)
        // return new Square(((((int) x) / squareSize) + 1),
        // 8 - ((int) y / squareSize));
        // else
        // return new Square(8 - (((int) x) / squareSize), (int) y / squareSize + 1);

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
     * the outer {@link VBox} that encases the {@link Board}.
     * 
     * @param square   The {@link Square} to get the x value for
     * @param relative Whether or not to treat the x value as relative
     * @return The x location of the top left of the {@link Square}
     */
    public double getXBySquare(Square square, boolean relative) {

        double rel = 0;

        if (relative) {

            Bounds bds = piecePane.localToScene(getBoundsInParent());

            rel = bds.getMinX();

        }

        if (!flipped)
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
     * the outer {@link VBox} that encases the {@link Board}.
     * 
     * @param square   The {@link Square} to get the y value for
     * @param relative Whether or not to treat the y value as relative
     * @return The y location of the top left of the {@link Square}
     */
    public double getYBySquare(Square square, boolean relative) {

        double rel = 0;

        if (relative) {

            Bounds bds = stack.localToScene(getBoundsInParent());

            rel = bds.getMinY();

        }

        if (!flipped)
            return ((squareSize * 7) - ((square.getRank() - 1) * squareSize)) - rel;
        else
            return ((square.getRank() - 1) * squareSize) - rel;

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

        squarePane.getChildren().clear();

        for (int r = 8; r > 0; r--) {

            for (int f = 8; f > 0; f--) {

                Region sq = new Region();
                sq.setPrefSize(squareSize, squareSize);
                sq.setMinSize(squareSize, squareSize);

                Square square = new Square(f, r);

                sq.setLayoutX(getXBySquare(square));
                sq.setLayoutY(getYBySquare(square));

                if (square.isLightSquare())
                    sq.setId("lightSquare");
                else
                    sq.setId("darkSquare");

                if (square.getFile() == 1 && square.getRank() == 1)
                    sq.setStyle("-fx-background-radius: 0 0 0 10; -fx-border-radius: 0 0 0 10;");
                else if (square.getFile() == 8 && square.getRank() == 1)
                    sq.setStyle("-fx-background-radius: 0 0 10 0; -fx-border-radius: 0 0 10 0;");
                else if (square.getFile() == 8 && square.getRank() == 8)
                    sq.setStyle("-fx-background-radius: 0 10 0 0; -fx-border-radius: 0 10 0 0;");
                else if (square.getFile() == 1 && square.getRank() == 8)
                    sq.setStyle("-fx-background-radius: 10 0 0 0; -fx-border-radius: 10 0 0 0;");

                squarePane.getChildren().add(sq);

            }

        }

    }

    // Event Handlers

    @Override
    public void onPlayerEvent(GameEvent event) {
        if (game == null)
            return;
        Platform.runLater(() -> {

            if (event.getType() == GameEvent.TYPE_MOVE) {

                currentPos = event.getCurrIndex();

                // if (color == TWO_PLAYER)
                // flipBoard();

                boardUpdated(true, event.getPrev(), event.getCurr(), event.getPrevIndex() > event.getCurrIndex());

                movePane.boardUpdated();
                movePane.posChanged(currentPos);

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

                if (game == null)
                    return;

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
