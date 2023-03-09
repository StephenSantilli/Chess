package guifx;

import java.util.ArrayList;
import java.util.EventListener;

import game.BoardListener;
import game.BoardMoveListener;
import game.Game;
import game.Move;
import game.Piece;
import game.Position;
import game.Square;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Board extends StackPane implements BoardMoveListener {

    private int pieceSize = 90;

    public int getPieceSize() {
        return pieceSize;
    }

    private int squareSize = 100;

    private Game game;

    public Game getGame() {
        return game;
    }

    private VBox sqPane;
    private Canvas sqModifierPane;
    private Canvas borderPane;
    private Pane piecePane;
    private Canvas movesPane;

    private ArrayList<PieceTranscoder> transcoderPieces;

    private ArrayList<GUIPiece> pieces;

    private GUIPiece dragging;
    public void setDragging(GUIPiece dragging) {
        this.dragging = dragging;
    }

    public GUIPiece getDragging() {
        return dragging;
    }

    private GUIPiece active;

    public void setActive(GUIPiece active) {
        this.active = active;
    }

    public GUIPiece getActive() {
        return active;
    }

    private MovePane mp;

    public MovePane getMp() {
        return mp;
    }

    private static final Color SQUARE_DARK = Color.rgb(155, 182, 124, 1);
    private static final Color SQUARE_LIGHT = Color.rgb(245, 241, 218, 1);
    private static final Color SQUARE_ACTIVE = Color.rgb(238, 187, 77, .70);
    private static final Color SQUARE_PREV_MOVE = Color.rgb(238, 187, 85, .70);
    private static final Color SQUARE_BORDER = Color.rgb(200, 200, 200, .5);
    private static final Color ATTACK_INDICATOR_COLOR = Color.rgb(100, 100, 100, .4);

    public EventHandler<KeyEvent> keyHandler = new EventHandler<KeyEvent>() {

        @Override
        public void handle(KeyEvent e) {

            if (e.getCode() == (KeyCode.LEFT)) {
                if (game.getCurrentPos() > 0) {
                    game.setCurrentPos(game.getCurrentPos() - 1);
                    boardUpdated(true, game.getPositions().get(game.getCurrentPos() + 1), game.getActivePos(), true);
                }

            } else if (e.getCode() == (KeyCode.RIGHT)) {

                if (game.getPositions().size() - 1 > game.getCurrentPos()) {
                    game.setCurrentPos(game.getCurrentPos() + 1);
                    boardUpdated(true, game.getPositions().get(game.getCurrentPos() - 1), game.getActivePos(), false);
                }

            } else if (e.getCode() == KeyCode.DOWN) {

                game.setCurrentPos(game.getPositions().size() - 1);
                boardUpdated();

            } else if (e.getCode() == KeyCode.UP) {

                game.setCurrentPos(0);
                boardUpdated();

            }

        }

    }; 

    public Board(int width, int height) throws Exception {

       

        this.game = new Game();
        game.addMoveListener(this);

        this.mp = new MovePane(game);
        mp.initMovePane();

        initSquares();
        getChildren().add(sqPane);

        sqModifierPane = new Canvas(squareSize * 8, squareSize * 8);
        drawModifierSq();
        getChildren().add(sqModifierPane);

        borderPane = new Canvas(squareSize * 8, squareSize * 8);
        clearBorder();
        getChildren().add(borderPane);

        movesPane = new Canvas(squareSize * 8, squareSize * 8);
        getChildren().add(movesPane);
        drawMovesPane();

        piecePane = new Pane();
        getChildren().add(piecePane);

        initPieceTranscoders();
        drawPieces(false, null, null);

        setOnMouseReleased(e -> {

            if (dragging != null) {
                dragging.onMouseReleased(e);
            } else {

                GUIPiece found = getGUIPieceAtSquare(getSquareByLoc((int) e.getSceneX(), (int) e.getSceneY()));
                if (found != null)
                    found.onMouseReleased(e);
                else if (active != null) {
                    active.onMouseReleased(e);
                }

            }

            e.consume();

        });

        setOnMouseDragged(e -> {

            if (dragging != null) {

                dragging.onMouseDragged(e);

            } else {

                GUIPiece found = getGUIPieceAtSquare(getSquareByLoc((int) e.getSceneX(), (int) e.getSceneY()));
                if (found != null)
                    found.onMouseDragged(e);

            }

            e.consume();

        });

        setOnMousePressed(e -> {

            if (dragging != null) {

                dragging.onMousePressed(e);

            } else {

                GUIPiece found = getGUIPieceAtSquare(getSquareByLoc((int) e.getSceneX(), (int) e.getSceneY()));
                if (found != null)
                    found.onMousePressed(e);

            }

            e.consume();

        });

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

        if (active == null)
            return;

        ArrayList<Move> pMoves = game.getActivePos().getPieceMoves(active.getPiece());

        gc.setFill(ATTACK_INDICATOR_COLOR);
        for (Move m : pMoves) {

            if (m.isCapture() && m.getCaptureSquare().equals(m.getDestination())) {
                gc.setStroke(ATTACK_INDICATOR_COLOR);
                gc.setLineWidth(squareSize * 0.04);
                gc.strokeOval(getXBySquare(m.getDestination()) + (squareSize * 0.05),
                        getYBySquare(m.getDestination()) + (squareSize * 0.05),
                        squareSize - (squareSize * .1),
                        squareSize - (squareSize * .1));

            } else
                gc.fillOval(getXBySquare(m.getDestination()) + (squareSize / 3.0),
                        getYBySquare(m.getDestination()) + (squareSize / 3.0), squareSize / 3.0, squareSize / 3.0);

        }

    }

    private void drawModifierSq() {

        GraphicsContext gc = sqModifierPane.getGraphicsContext2D();
        gc.setFill(Color.TRANSPARENT);
        gc.clearRect(0.0, 0.0, sqModifierPane.getLayoutBounds().getWidth(),
                sqModifierPane.getLayoutBounds().getHeight());

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
        this.sqPane = new VBox();

        boolean dark = false;
        for (int r = 0; r < 8; r++) {

            HBox hbox = new HBox();

            for (int c = 0; c < 8; c++, dark = !dark) {

                Rectangle sq = new Rectangle(100, 100, dark ? SQUARE_DARK : SQUARE_LIGHT);

                StackPane pane = new StackPane(sq);

                hbox.getChildren().add(pane);

            }
            dark = !dark;

            sqPane.getChildren().add(hbox);
        }

    }

    private void pieceMoveAnimation(GUIPiece gp, Square origin, Square destination, Piece cp) {
        ImageView img = gp.getImage();

        TranslateTransition t = new TranslateTransition(Duration.millis(100), img);

        t.setFromX((origin.getFile() - 1) * squareSize + ((squareSize - pieceSize) / 2.0));
        t.setFromY(700 - ((origin.getRank() - 1) * squareSize) + ((squareSize - pieceSize) / 2.0));
        t.setToX((destination.getFile() - 1) * squareSize + ((squareSize - pieceSize) / 2.0));
        t.setToY(700 - ((destination.getRank() - 1) * squareSize) + ((squareSize - pieceSize) / 2.0));

        if (cp != null) {

            ImageView i = getPieceTranscoder(cp).getImageView();

            piecePane.getChildren().add(i);

            GUIPiece guiP = new GUIPiece(cp, i, this);
            // pieces.add(guiP);

            i.setLayoutX(getXBySquare(cp.getSquare()) + ((squareSize - pieceSize) / 2.0));
            i.setLayoutY(((getYBySquare(cp.getSquare()))) + ((squareSize - pieceSize) / 2.0));

            t.getNode().toFront();

            t.setOnFinished(e -> {

                piecePane.getChildren().remove(guiP.getImage());

            });

        }

        t.play();
    }

    private void drawPieces(boolean animate, Position p1, Position p2) {
        drawPieces(animate, p1, p2, false);
    }

    private void drawPieces(boolean animate, Position p1, Position p2, boolean backward) {

        this.pieces = new ArrayList<GUIPiece>();
        piecePane.getChildren().clear();

        for (int r = 0; r < 8; r++) {

            for (int c = 0; c < 8; c++) {

                Piece p = null;
                if (p2 != null)
                    p = p2.getPieceAtSquare(new Square(r + 1, c + 1));
                else
                    p = game.getActivePos().getPieceAtSquare(new Square(r + 1, c + 1));

                if (p == null)
                    continue;

                ImageView img = getPieceTranscoder(p).getImageView();

                piecePane.getChildren().add(img);

                GUIPiece guiP = new GUIPiece(p, img, this);
                pieces.add(guiP);

                if (animate && p1 != null && p2 != null
                        && ((!backward && p2.getMove().getDestination().equals(p.getSquare()))
                                || (backward && p1.getMove().getOrigin().equals(p.getSquare())))) {

                    if (!backward) {
                        pieceMoveAnimation(guiP, p2.getMove().getOrigin(), p2.getMove().getDestination(),
                                p2.getMove().getCapturePiece());
                    } else {
                        pieceMoveAnimation(guiP, p1.getMove().getDestination(), p1.getMove().getOrigin(),
                                p1.getMove().getCapturePiece());
                    }

                } else {

                    img.setLayoutX(r * squareSize + ((squareSize - pieceSize) / 2.0));
                    img.setLayoutY(700 - (c * squareSize) + ((squareSize - pieceSize) / 2.0));

                }

            }
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

    public Square getSquareByLoc(int x, int y) {

        Bounds b = localToScene(getBoundsInLocal());
        int relativeX = (int) b.getMinX();
        int relativeY = (int) b.getMinY();

        return new Square((((x - relativeX) / squareSize) + 1), (((squareSize * 8 - y + relativeY)) / squareSize) + 1);

    }

    public int getXBySquare(Square sq) {

        return (sq.getFile() - 1) * squareSize;

    }

    public int getYBySquare(Square sq) {

        return (squareSize * 7) - ((sq.getRank() - 1) * squareSize);

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

    public void updateActive() {
        drawModifierSq();
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
        drawPieces(animate, p1, p2, backward);
        if (game.getActivePos().getMove() != null && game.getActivePos().getMove().getPromoteType() == '?') {

            try {
                PromoteDialog pD = new PromoteDialog(game, pieceSize);
                pD.setOnCloseRequest(e -> {

                    game.setPromo(pD.getResult());
                    pD.hide();
                    boardUpdated();

                });

                pD.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public int getSquareSize() {
        return squareSize;
    }

    @Override
    public void moveMade() {
        // TODO Auto-generated method stub
    }

    @Override
    public void undoMove() {
        boardUpdated(true, game.getActivePos().getRedo(), game.getActivePos(), true);
    }

    @Override
    public void resetMoves() {
        // TODO Auto-generated method stub

    }

    @Override
    public void posChanged() {
        // TODO Auto-generated method stub

    }

    @Override
    public void redoMove() {
        boardUpdated(true, game.getPositions().get(game.getCurrentPos() - 1), game.getActivePos(), false);

    }

}
