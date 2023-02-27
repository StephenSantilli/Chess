package guifx;

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

public class Board extends StackPane {

    private int pieceSize = 80;
    private int squareSize = 100;

    private VBox squares;
    private Pane pieces;

    private void initSquares() {
        this.squares = new VBox();

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

            squares.getChildren().add(hbox);
        }

    }

    public Board(int width, int height) throws Exception {

        initSquares();

        getChildren().add(squares);

        pieces = new Pane();
        

        for (int r = 0; r < 8; r++) {

            for (int c = 0; c < 8; c++) {

                SVGTranscoder svg = new SVGTranscoder(pieceSize, "BB");
                ImageView img = svg.getImageView();

                pieces.getChildren().add(img);

                img.setLayoutX(r * squareSize + ((squareSize - pieceSize) / 2.0));
                img.setLayoutY(c * squareSize + ((squareSize - pieceSize) / 2.0));

                img.setOnMouseDragged(ev -> {

                    img.toFront();

                    img.setX(ev.getX() - (pieceSize / 2.0));
                    img.setY(ev.getY() - (pieceSize / 2.0));

                });

                img.setOnMouseDragReleased(ev -> {

                });

            }
        }
        getChildren().add(pieces);
        squares.requestFocus();

        setOnMousePressed(e -> {

            lastrect.fireEvent(e);

        });

    }

}
