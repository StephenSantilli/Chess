package guifx;

import java.util.ArrayList;

import game.BoardListener;
import game.Game;
import game.Move;
import game.Piece;
import game.Square;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board extends StackPane implements BoardListener {

    private int pieceSize = 80;
    private int squareSize = 100;

    private Game game;

    private VBox sqPane;
    private Pane piecePane;

    private ArrayList<PieceTranscoder> transcoderPieces;

    private ArrayList<GUIPiece> pieces;

    private GUIPiece dragging;

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

    private void initSquares() {
        this.sqPane = new VBox();

        boolean dark = false;
        for (int r = 0; r < 8; r++) {

            HBox hbox = new HBox();

            for (int c = 0; c < 8; c++, dark = !dark) {

                Rectangle sq = new Rectangle(100, 100, dark ? Color.GREEN : Color.WHITE);

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

    public Board(int width, int height) throws Exception {

        initSquares();

        getChildren().add(sqPane);

        piecePane = new Pane();

        initPieceTranscoders();

        this.game = new Game();
        game.addListener(this);

        this.pieces = new ArrayList<GUIPiece>();

        for (int r = 0; r < 8; r++) {

            for (int c = 0; c < 8; c++) {

                Piece p = game.getActivePos().getPieceAtSquare(new Square(r + 1, c + 1));

                if (p == null)
                    continue;

                ImageView img = getPieceTranscoder(p).getImageView();

                piecePane.getChildren().add(img);

                img.setLayoutX(r * squareSize + ((squareSize - pieceSize) / 2.0));
                img.setLayoutY(c * squareSize + ((squareSize - pieceSize) / 2.0));

                GUIPiece guiP = new GUIPiece(p, img);
                pieces.add(guiP);

                img.setOnMouseDragged(ev -> {

                    img.toFront();
                    dragging = guiP;

                    img.setX(ev.getX() - (pieceSize / 2.0));
                    img.setY(ev.getY() - (pieceSize / 2.0));

                });

                img.setOnMouseDragReleased(ev -> {

                    if (dragging == null)
                        return;
                    try {

                        game.makeMove(new Move(dragging.getPiece().getSquare(),
                                getSquareByLoc((int) ev.getX(), (int) ev.getY()), game.getActivePos()));
                    } catch (Exception e) {

                    }
                    dragging = null;

                });

            }
        }
        
        getChildren().add(piecePane);
        sqPane.requestFocus();

        setOnMousePressed(e -> {

        });

    }

    public static Square getSquareByLoc(int x, int y) {

        return new Square((x / 100) + 1, (((800 - y)) / 100) + 1);

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

    }

    @Override
    public char promptForPromote(Move move) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'promptForPromote'");
    }

}
