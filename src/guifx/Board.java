package guifx;

import java.util.ArrayList;

import game.BoardMoveListener;
import game.Game;
import game.Move;
import game.Piece;
import game.Position;
import game.Square;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Board extends VBox implements BoardMoveListener {

    private static final Color SQUARE_DARK = Color.rgb(155, 182, 124, 1);
    private static final Color SQUARE_LIGHT = Color.rgb(245, 241, 218, 1);
    private static final Color SQUARE_ACTIVE = Color.rgb(238, 187, 77, .70);
    private static final Color SQUARE_PREV_MOVE = Color.rgb(238, 187, 85, .70);
    private static final Color SQUARE_BORDER = Color.rgb(200, 200, 200, .5);
    private static final Color ATTACK_INDICATOR_COLOR = Color.rgb(100, 100, 100, .4);

    private int pieceSize = 90;
    private int squareSize = 100;

    private Game game;

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

    private ArrayList<GUIPiece> pieces;
    private ArrayList<PieceTranscoder> transcoderPieces;

    private GUIPiece active;
    private GUIPiece dragging;

    private boolean flipped;
    private boolean white;

    private ArrayList<TranslateTransition> transitions;

    public EventHandler<KeyEvent> keyHandler = new EventHandler<KeyEvent>() {

        @Override
        public void handle(KeyEvent e) {

            if (e.getCode() == (KeyCode.LEFT)) {
                if (game.getCurrentPos() > 0) {
                    game.setCurrentPos(game.getCurrentPos() - 1);
                }

            } else if (e.getCode() == (KeyCode.RIGHT)) {

                if (game.getPositions().size() - 1 > game.getCurrentPos()) {
                    game.setCurrentPos(game.getCurrentPos() + 1);
                }

            } else if (e.getCode() == KeyCode.DOWN) {

                game.setCurrentPos(game.getPositions().size() - 1);

            } else if (e.getCode() == KeyCode.UP) {

                game.setCurrentPos(0);

            } else if (e.getCode() == KeyCode.ESCAPE) {

                active = null;
                dragging = null;
                updateActive();
                clearBorder();
                boardUpdated();

            }

        }

    };

    public Board(int squareSize, BarMenu menuBar) throws Exception {

        this.game = new Game();
        game.addMoveListener(this);

        this.squareSize = squareSize;

        this.menuBar = menuBar;
        initMenus();

        this.white = true;
        this.flipped = !white;

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
        movePane.initMovePane();

        scrollMovePane.setContent(movePane);
        game.addMoveListener(getMovePane());

        this.squarePane = new VBox();
        initSquares();

        squareHighlightPane = new Canvas(squareSize * 8, squareSize * 8);
        drawHighlightSq();

        borderPane = new Canvas(squareSize * 8, squareSize * 8);
        clearBorder();

        movesPane = new Canvas(squareSize * 8, squareSize * 8);
        drawMovesPane();

        piecePane = new Pane();
        initPieceTranscoders();

        stack.getChildren().addAll(squarePane, squareHighlightPane, borderPane, movesPane, piecePane);

        topTimerBox.setViewOrder(1);
        bottomTimerBox.setViewOrder(1);
        stack.setViewOrder(0);

        getChildren().addAll(topTimerBox, stack, bottomTimerBox);

        timerChange();

        setOnMouseMoved(e -> {
            setMouseType(e.getSceneX(), e.getSceneY());
        });

        setOnMouseReleased(e -> {

            if (e.getButton() != MouseButton.PRIMARY)
                return;

            if (dragging != null) {
                dragging.onMouseReleased(e);
            } else {

                GUIPiece found = getGUIPieceAtSquare(getSquareByLoc((int) e.getSceneX(), (int) e.getSceneY(), true));
                if (found != null)
                    found.onMouseReleased(e);
                else if (active != null) {
                    active.onMouseReleased(e);
                } else {
                    setActive(null);
                    setDragging(null);
                    updateActive();
                    clearBorder();
                }

            }
            setMouseType(e.getSceneX(), e.getSceneY());

        });

        setOnMouseDragged(e -> {

            if (e.getButton() != MouseButton.PRIMARY)
                return;

            if (dragging != null) {

                dragging.onMouseDragged(e);

            }

            setMouseType(e.getSceneX(), e.getSceneY());

        });

        setOnMousePressed(e -> {

            if (e.getButton() != MouseButton.PRIMARY)
                return;

            if (dragging != null) {

                dragging.onMousePressed(e);

            } else {

                GUIPiece found = getGUIPieceAtSquare(getSquareByLoc((int) e.getSceneX(), (int) e.getSceneY(), true));
                if (found != null)
                    found.onMousePressed(e);
                else if (active != null)
                    active.onMousePressed(e);

            }
            setMouseType(e.getSceneX(), e.getSceneY());

        });

    }

    public void newGame() {

        startGame(null);

    }

    private void initMenus() {

        viewMenu = new ViewMenu(this);
        gameMenu = new GameMenu(this);

        game.addMoveListener(gameMenu);

        menuBar.getMenus().addAll(gameMenu, viewMenu);

    }

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

    public void startGame(WindowEvent we) {

        GameSettingsDialog settings = new GameSettingsDialog(getScene().getWindow(), game);
        settings.setOnHidden(e -> {

            if (settings.getTimePerSide() > -1) {

                game.stopGame();
                game = new Game(settings.getTimePerSide(), settings.getTimePerMove());
                game.addMoveListener(this);
                game.addMoveListener(getMovePane());
                game.addMoveListener(gameMenu);

                game.startGame();
                boardUpdated();

            }

        });

        settings.showAndWait();

    }

    void clearBorder() {
        GraphicsContext gc = borderPane.getGraphicsContext2D();
        gc.setFill(Color.TRANSPARENT);
        gc.clearRect(0.0, 0.0, borderPane.getLayoutBounds().getWidth(),
                borderPane.getLayoutBounds().getHeight());
    }

    void drawBorder(double x, double y) {

        clearBorder();
        GraphicsContext gc = borderPane.getGraphicsContext2D();
        gc.setStroke(SQUARE_BORDER);
        double strokeWidth = squareSize / 20.0;
        gc.setLineWidth(strokeWidth);
        gc.strokeRect(x + (strokeWidth / 2.0), y + (strokeWidth / 2.0), squareSize - strokeWidth,
                squareSize - strokeWidth);

    }

    private void drawMovesPane() {

        GraphicsContext gc = movesPane.getGraphicsContext2D();
        gc.setFill(Color.TRANSPARENT);
        gc.clearRect(0.0, 0.0, movesPane.getLayoutBounds().getWidth(),
                movesPane.getLayoutBounds().getHeight());

        if (active == null || game.getCurrentPos() != game.getPositions().size() - 1 || game.getResult() > 0)
            return;

        ArrayList<Move> pMoves = game.getActivePos().getPieceMoves(active.getPiece());

        gc.setFill(ATTACK_INDICATOR_COLOR);
        gc.setStroke(ATTACK_INDICATOR_COLOR);
        gc.setLineWidth(squareSize * 0.04);
        for (Move m : pMoves) {

            if (m.isCapture() && m.getCaptureSquare().equals(m.getDestination())) {

                gc.strokeOval(getXBySquare(m.getDestination()) + (squareSize * 0.05),
                        getYBySquare(m.getDestination()) + (squareSize * 0.05),
                        squareSize - (squareSize * .1),
                        squareSize - (squareSize * .1));

            } else
                gc.fillOval(getXBySquare(m.getDestination()) + (squareSize / 3.0),
                        getYBySquare(m.getDestination()) + (squareSize / 3.0), squareSize / 3.0, squareSize / 3.0);

        }

    }

    private void drawHighlightSq() {

        GraphicsContext gc = squareHighlightPane.getGraphicsContext2D();
        gc.setFill(Color.TRANSPARENT);
        gc.clearRect(0.0, 0.0, squareHighlightPane.getLayoutBounds().getWidth(),
                squareHighlightPane.getLayoutBounds().getHeight());

        gc.setFill(SQUARE_PREV_MOVE);
        if (game.getCurrentPos() > 0) {

            Position pos = game.getActivePos();
            // prev move origin square
            Square origin = pos.getMove().getOrigin();
            gc.fillRect(getXBySquare(origin), getYBySquare(origin), squareSize, squareSize);

            // prev move dest square
            Square destination = pos.getMove().getDestination();
            gc.fillRect(getXBySquare(destination), getYBySquare(destination), squareSize, squareSize);
        }

        gc.setFill(SQUARE_ACTIVE);
        // active piece square
        if (active != null) {

            gc.fillRect(getXBySquare(active.getPiece().getSquare()), getYBySquare(
                    active.getPiece().getSquare()), squareSize, squareSize);

        } else if (dragging != null) {

            gc.fillRect(getXBySquare(dragging.getPiece().getSquare()), getYBySquare(
                    dragging.getPiece().getSquare()), squareSize, squareSize);

        }

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

    private void setMouseType(double mouseX, double mouseY) {

        if (dragging != null) {
            setCursor(Cursor.CLOSED_HAND);

        } else if (getSquareByLoc(mouseX, mouseY, true).isValid()
                && game.getActivePos().getPieceAtSquare(getSquareByLoc(mouseX, mouseY, true)) != null) {

            setCursor(Cursor.OPEN_HAND);

        } else {
            setCursor(Cursor.DEFAULT);
        }

    }

    private void pieceMoveAnimation(GUIPiece gp, Square origin, Square destination, Piece capture) {

        ImageView img = gp.getImage();

        TranslateTransition t = new TranslateTransition(Duration.millis(100), img);

        double fromX = getXBySquare(origin) + ((squareSize - pieceSize) / 2.0);
        double toX = getXBySquare(destination) + ((squareSize - pieceSize) / 2.0);
        double fromY = getYBySquare(origin) + ((squareSize - pieceSize) / 2.0);
        double toY = getYBySquare(destination) + ((squareSize - pieceSize) / 2.0);

        t.setFromX(fromX - toX);
        t.setFromY(fromY - toY);
        t.setToX(0);
        t.setToY(0);

        if (capture != null) {

            ImageView i = getPieceTranscoder(capture).getImageView();

            piecePane.getChildren().add(i);

            GUIPiece guiP = new GUIPiece(capture, i, this, stack);

            i.setLayoutX(getXBySquare(capture.getSquare()) + ((squareSize - pieceSize) / 2.0));
            i.setLayoutY(((getYBySquare(capture.getSquare()))) + ((squareSize - pieceSize) / 2.0));

            t.getNode().toFront();

            t.setOnFinished(e -> {

                piecePane.getChildren().remove(guiP.getImage());

            });

        }

        transitions.add(t);
    }

    void drawPieces(boolean animate, Position p1, Position p2) {
        drawPieces(animate, p1, p2, false);
    }

    /**
     * Clears the old pieces and draws new pieces on the board.
     * 
     * @param animate  Whether or not the move should be animated. Must be
     *                 {@code false} if the positions are not back to back.
     * @param p1       The position before the board was updated.
     * @param p2       The position after the board was updated. If {@code null},
     *                 the active position of the game will be used instead.
     * @param backward {@code true} if {@code p2} is before {@code p1} (such as when
     *                 undoing.)
     */
    private void drawPieces(boolean animate, Position p1, Position p2, boolean backward) {

        this.pieces = new ArrayList<GUIPiece>();
        piecePane.getChildren().clear();
        transitions = new ArrayList<TranslateTransition>();

        if (p2 == null)
            p2 = game.getActivePos();

        for (int r = 0; r < 8; r++) {

            for (int c = 0; c < 8; c++) {

                Piece p = p2.getPieceAtSquare(new Square(r + 1, c + 1));

                if (p == null)
                    continue;

                ImageView img = getPieceTranscoder(p).getImageView();
                piecePane.getChildren().add(img);

                GUIPiece guiP = new GUIPiece(p, img, this, stack);
                pieces.add(guiP);

                img.setLayoutX(getXBySquare(p.getSquare()) + ((squareSize - pieceSize) / 2.0));
                img.setLayoutY(getYBySquare(p.getSquare()) + ((squareSize - pieceSize) / 2.0));

                if (animate && p1 != null && p2 != null
                // Either not backwards and the piece in the move of p2 is this piece
                        && ((!backward && p2.getMove().getDestination().equals(p.getSquare()))
                                // Or it is backwards and the piece in the move of p1 is this piece
                                || (backward && p1.getMove().getOrigin().equals(p.getSquare()))
                                || (!backward && p2.getMove().isCastle()
                                        && p2.getMove().getRookDestination().equals(p.getSquare()))
                                || (backward && p1.getMove().isCastle()
                                        && p1.getMove().getRookOrigin().equals(p.getSquare())))) {

                    if (!backward) {

                        if (p2.getMove().isCastle() && p2.getMove().getRookDestination().equals(p.getSquare())) {

                            pieceMoveAnimation(guiP, p2.getMove().getRookOrigin(),
                                    p2.getMove().getRookDestination(),
                                    null);

                        } else
                            pieceMoveAnimation(guiP, p2.getMove().getOrigin(), p2.getMove().getDestination(),
                                    p2.getMove().getCapturePiece());

                    } else {

                        if (p1.getMove().isCastle() && p1.getMove().getRookOrigin().equals(p.getSquare())) {
                            pieceMoveAnimation(guiP, p1.getMove().getRookDestination(), p1.getMove().getRookOrigin(),
                                    null);
                        } else
                            pieceMoveAnimation(guiP, p1.getMove().getDestination(), p1.getMove().getOrigin(),
                                    p1.getMove().getCapturePiece());

                    }

                }

            }
        }

        for (TranslateTransition t : transitions) {
            t.play();
        }

    }

    public GUIPiece getGUIPieceAtSquare(Square square) {

        GUIPiece found = null;
        for (int i = 0; i < pieces.size() && found == null; i++) {

            if (pieces.get(i).getPiece().getSquare().equals(square))
                found = pieces.get(i);

        }
        return found;

    }

    public void flipBoard() {

        flipped = !flipped;
        boardUpdated();

    }

    public Square getSquareByLoc(double x, double y) {
        return getSquareByLoc(x, y, false);
    }

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

    public int getXBySquare(Square sq) {
        return getXBySquare(sq, false);
    }

    public int getXBySquare(Square sq, boolean relative) {
        int rel = 0;
        if (relative) {
            Bounds bds = stack.localToScene(getBoundsInParent());
            rel = (int) bds.getMinX();
        }

        if (!flipped)
            return (int) (((sq.getFile() - 1) * squareSize)) - rel;
        else
            return (int) ((squareSize * 7) - ((sq.getFile() - 1) * squareSize)) - rel;

    }

    public int getYBySquare(Square sq) {
        return getYBySquare(sq, false);
    }

    public int getYBySquare(Square sq, boolean relative) {
        int rel = 0;
        if (relative) {
            Bounds bds = stack.localToScene(getBoundsInParent());
            rel = (int) bds.getMinY();
        }

        // img.setLayoutY((squareSize * 7) - (c * squareSize) + ((squareSize -
        // pieceSize) / 2.0));
        if (!flipped)
            return (int) ((squareSize * 7) - ((sq.getRank() - 1) * squareSize)) - rel;
        else
            return (int) (((sq.getRank() - 1) * squareSize)) - rel;
        // return (7 - sq.getRank()) * squareSize - rel;

    }

    private PieceTranscoder getPieceTranscoder(Piece p) {

        PieceTranscoder found = null;

        for (int i = 0; i < transcoderPieces.size() && found == null; i++) {

            PieceTranscoder pt = transcoderPieces.get(i);
            if (pt.isColor() == p.isWhite() && pt.getPieceCode() == p.getCode())
                found = pt;

        }

        return found;

    }

    /**
     * Updates the square highlights and moves panes.
     */
    public void updateActive() {
        drawHighlightSq();
        drawMovesPane();
    }

    public void boardUpdated() {
        boardUpdated(false, null, null);
    }

    public void boardUpdated(boolean animate, Position p1, Position p2) {
        boardUpdated(animate, p1, p2, false);
    }

    public void boardUpdated(boolean animate, Position p1, Position p2, boolean backward) {

        updateActive();
        viewMenu.boardUpdated();

        boolean ani = animate && dragging == null;
        dragging = null;

        drawPieces(ani, p1, p2, backward);

        topTimer.setWhite(!flipped);
        topTimer.update();

        bottomTimer.setWhite(flipped);
        bottomTimer.update();

        gameMenu.updatePauseResume();

        if (game.getActivePos().getMove() != null && game.getActivePos().getMove().getPromoteType() == '?') {

            try {

                showPromoteDialog();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void showPromoteDialog() throws Exception {

        PromoteDialog pD = new PromoteDialog(pieceSize, squareSize, !game.getActivePos().isWhite(), flipped,
                getScene().getWindow());

        pD.setOnHidden(e -> {

            if (pD.getResult() == 'X') {
                game.undoMove();
            } else {
                game.setPromo(pD.getResult());
            }
            pD.hide();
            boardUpdated();

        });

        Bounds bds = stack.localToScreen(getBoundsInParent());
        pD.setX(bds.getMinX() + getXBySquare(game.getActivePos().getMove().getDestination()));
        pD.setY(bds.getMinY() + getYBySquare(game.getActivePos().getMove().getDestination())
                - ((!game.getActivePos().isWhite() && !flipped) || (game.getActivePos().isWhite() && flipped)
                        ? -squareSize
                        : squareSize * (4 + (1 / 3.0))));

        pD.show();
        pD.sizeToScene();

    }

    public int getSquareSize() {
        return squareSize;
    }

    @Override
    public void moveMade() {

    }

    @Override
    public void undoMove() {

    }

    @Override
    public void resetMoves() {

        boardUpdated();

    }

    @Override
    public void posChanged(int old, int curr) {

        Position o = null;
        if (old > game.getPositions().size() - 1) {
            if (game.getLastPos().getRedo() != null)
                o = game.getLastPos().getRedo();
        } else {
            o = game.getPositions().get(old);
        }

        Position n = game.getPositions().get(curr);

        boardUpdated((int) Math.abs(old - curr) == 1, o, n, old >= curr);

    }

    @Override
    public void redoMove() {

    }

    @Override
    public void timerChange() {
        Platform.runLater(() -> {
            topTimer.update();
            bottomTimer.update();
        });

    }

    @Override
    public void gameOver() {
        // TODO Auto-generated method stub
        Platform.runLater(() -> {
            if (game.getResult() <= Game.RESULT_IN_PROGRESS || game.getResult() == Game.RESULT_TERMINATED)
                return;

            Dialog<Void> over = new Dialog<Void>();
            over.setTitle("Game Over");

            String msg = "";
            if (game.getResult() == Game.RESULT_DRAW) {
                msg = "Draw.";
            } else if (game.getResult() == Game.RESULT_BLACK_WIN) {
                msg = "Black win.";
            } else if (game.getResult() == Game.RESULT_WHITE_WIN) {
                msg = "White win.";
            }

            msg += game.getResultReason();

            over.setContentText(msg);

            over.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

            over.showAndWait();
        });

    }

    @Override
    public void pauseGame() {
        timerChange();
    }

    @Override
    public void resumeGame() {
        // TODO Auto-generated method stub
    }

    // Getters/Setters

    public int getPieceSize() {
        return pieceSize;
    }

    public BarMenu getMenuBar() {
        return menuBar;
    }

    public boolean isWhite() {
        return white;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public GUITimer getTopTimer() {
        return topTimer;
    }

    public Game getGame() {
        return game;
    }

    public void setDragging(GUIPiece dragging) {
        this.dragging = dragging;
    }

    public GUIPiece getDragging() {
        return dragging;
    }

    public void setActive(GUIPiece active) {
        this.active = active;
    }

    public GUIPiece getActive() {
        return active;
    }

    public MovePane getMovePane() {
        return movePane;
    }

    public ScrollPane getScrollMovePane() {
        return scrollMovePane;
    }

}
