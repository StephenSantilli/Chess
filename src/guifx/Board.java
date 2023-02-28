package guifx;

import java.util.ArrayList;

import game.BoardListener;
import game.Game;
import game.Move;
import game.Piece;
import game.Position;
import game.Square;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board extends StackPane implements BoardListener {

    private int pieceSize = 95;
    private int squareSize = 100;

    private Game game;

    private VBox sqPane;
    private Canvas sqModifierPane;
    private Pane piecePane;

    private ArrayList<PieceTranscoder> transcoderPieces;

    private ArrayList<GUIPiece> pieces;

    private GUIPiece dragging;
    private GUIPiece active;

    private static final Color SQUARE_DARK = Color.rgb(155, 182, 124, 1);
    private static final Color SQUARE_LIGHT = Color.rgb(245, 241, 218, 1);
    private static final Color SQUARE_ACTIVE = Color.rgb(238, 187, 77, .70);
    private static final Color ATTACK_INDICATOR_COLOR = Color.rgb(100, 100, 100, .4);

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

    private void drawModifierSq() {

        GraphicsContext gc = sqModifierPane.getGraphicsContext2D();
        gc.setFill(Color.TRANSPARENT);
        gc.clearRect(0.0, 0.0, sqModifierPane.getLayoutBounds().getWidth(),
                sqModifierPane.getLayoutBounds().getHeight());

        gc.setFill(SQUARE_ACTIVE);
        if (game.getCurrentPos() > 0) {

            Position pos = game.getActivePos();
            // prev move origin square
            Square origin = pos.getMove().getOrigin();
            gc.fillRect(getXBySquare(origin), getYBySquare(origin), squareSize, squareSize);

            // prev move dest square
            Square destination = pos.getMove().getDestination();
            gc.fillRect(getXBySquare(destination), getYBySquare(destination), squareSize, squareSize);
        }

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

                pane.setOnMousePressed(ev -> {

                    sq.setFill(Color.AQUA);

                });

            }
            dark = !dark;

            sqPane.getChildren().add(hbox);
        }

    }

    private void drawPieces() {

        this.pieces = new ArrayList<GUIPiece>();
        piecePane.getChildren().clear();

        for (int r = 0; r < 8; r++) {

            for (int c = 0; c < 8; c++) {

                Piece p = game.getActivePos().getPieceAtSquare(new Square(r + 1, c + 1));

                if (p == null)
                    continue;

                ImageView img = getPieceTranscoder(p).getImageView();

                piecePane.getChildren().add(img);

                img.setLayoutX(r * squareSize + ((squareSize - pieceSize) / 2.0));
                img.setLayoutY(700 - (c * squareSize) + ((squareSize - pieceSize) / 2.0));

                GUIPiece guiP = new GUIPiece(p, img);
                pieces.add(guiP);

                img.setOnMousePressed(ev -> {

                    if (active != null) {

                        if (!game.getActivePos().canPieceMoveToSquare(active.getPiece(), guiP.getPiece().getSquare())) {

                            active = guiP;

                        }

                    }

                    img.toFront();
                    dragging = guiP;

                    img.setX(ev.getX() - (pieceSize / 2.0));
                    img.setY(ev.getY() - (pieceSize / 2.0));

                    drawModifierSq();
                    ev.consume();

                });

                img.setOnMouseDragged(ev -> {

                    img.toFront();
                    dragging = guiP;
                    active = guiP;

                    img.setX(ev.getX() - (pieceSize / 2.0));
                    img.setY(ev.getY() - (pieceSize / 2.0));

                    ev.consume();

                });

                img.setOnMouseReleased(ev -> {

                    if (dragging == null && (active == null
                            || (active != null && active.getPiece().getSquare()
                                    .equals(getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()))))) {

                        dragging = null;

                        boardUpdated();
                        ev.consume();
                        return;

                    } else if (active != null && dragging != null && !active.getPiece().getSquare()
                            .equals(getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()))) {

                        dragging = null;

                    }

                    if (dragging != null) {

                        int cPos = game.getCurrentPos();
                        try {
                            game.makeMove(new Move(dragging.getPiece().getSquare(),
                                    getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()), game.getActivePos()));
                            dragging = null;
                            active = null;
                        } catch (Exception e) {

                        }

                        if (cPos == game.getCurrentPos()) {
                            GUIPiece pc = getGUIPieceAtSquare(
                                    getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()));
                            if (pc != null) {
                                active = pc;
                                dragging = null;
                            }
                        }

                    } else if (active != null) {

                        int cPos = game.getCurrentPos();
                        try {
                            game.makeMove(new Move(active.getPiece().getSquare(),
                                    getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()), game.getActivePos()));
                            dragging = null;
                            active = null;
                        } catch (Exception e) {

                        }

                        if (cPos == game.getCurrentPos()) {
                            GUIPiece pc = getGUIPieceAtSquare(
                                    getSquareByLoc((int) ev.getSceneX(), (int) ev.getSceneY()));
                            if (pc != null) {
                                active = pc;
                                dragging = null;
                            }
                        }

                    }

                    boardUpdated();
                    ev.consume();

                });
            }
        }

    }

    public Board(int width, int height) throws Exception {

        this.game = new Game();
        game.addListener(this);

        initSquares();

        getChildren().add(sqPane);

        sqModifierPane = new Canvas(squareSize * 8, squareSize * 8);
        drawModifierSq();

        getChildren().add(sqModifierPane);

        piecePane = new Pane();



        getChildren().add(piecePane);

        initPieceTranscoders();
        drawPieces();

        setOnMouseReleased(e -> {

            if (dragging != null) {
                dragging.getImage().fireEvent(e);
            } else {

                GUIPiece found = getGUIPieceAtSquare(getSquareByLoc((int) e.getX(), (int) e.getY()));
                if (found != null)
                    found.getImage().fireEvent(e);
                else if(active != null) {
                    active.getImage().fireEvent(e);
                }

            }

            e.consume();

        });

        setOnMouseDragged(e -> {

            if (dragging != null) {

                dragging.getImage().fireEvent(e);

            } else {

                GUIPiece found = getGUIPieceAtSquare(getSquareByLoc((int) e.getX(), (int) e.getY()));
                if (found != null)
                    found.getImage().fireEvent(e);

            }

            e.consume();

        });

        setOnMousePressed(e -> {

            if (dragging != null) {

                dragging.getImage().fireEvent(e);

            } else {

                GUIPiece found = getGUIPieceAtSquare(getSquareByLoc((int) e.getX(), (int) e.getY()));
                if (found != null)
                    found.getImage().fireEvent(e);

            }

            e.consume();

        });

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

        return new Square((x / squareSize) + 1, (((squareSize * 8 - y)) / squareSize) + 1);

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

    @Override
    public void boardUpdated() {
        // TODO Auto-generated method stub
        drawModifierSq();
        drawPieces();
    }

    @Override
    public char promptForPromote(Move move) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'promptForPromote'");
    }

}
