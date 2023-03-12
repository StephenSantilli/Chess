package guifx;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class PromoteDialog extends Stage {

    private char result;

    public char getResult() {
        return result;
    }

    public PromoteDialog(int pieceSize, int squareSize, boolean white, boolean flipped, Window owner) throws Exception {

        initStyle(StageStyle.UNDECORATED);
        this.result = 'X';
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);

        // getDialogPane().setMaxHeight(Double.MAX_VALUE);
        // getDialogPane().setMaxWidth(Double.MAX_VALUE);

        ImageView queen = new PieceTranscoder(pieceSize, white, 'Q').getImageView();
        ImageView rook = new PieceTranscoder(pieceSize, white, 'R').getImageView();
        ImageView bishop = new PieceTranscoder(pieceSize, white, 'B').getImageView();
        ImageView knight = new PieceTranscoder(pieceSize, white, 'N').getImageView();

        Pane q = new Pane(queen);
        q.setPrefHeight(squareSize);
        q.setPrefWidth(squareSize);
        queen.setLayoutX((squareSize - pieceSize) / 2.0);
        queen.setLayoutY((squareSize - pieceSize) / 2.0);

        Pane r = new Pane(rook);
        r.setPrefHeight(squareSize);
        r.setPrefWidth(squareSize);
        rook.setLayoutX((squareSize - pieceSize) / 2.0);
        rook.setLayoutY((squareSize - pieceSize) / 2.0);

        Pane b = new Pane(bishop);
        b.setPrefHeight(squareSize);
        b.setPrefWidth(squareSize);
        bishop.setLayoutX((squareSize - pieceSize) / 2.0);
        bishop.setLayoutY((squareSize - pieceSize) / 2.0);

        Pane n = new Pane(knight);
        n.setPrefHeight(squareSize);
        n.setPrefWidth(squareSize);
        knight.setLayoutX((squareSize - pieceSize) / 2.0);
        knight.setLayoutY((squareSize - pieceSize) / 2.0);

        queen.setOnMouseClicked(e -> {
            e.consume();
            result = 'Q';
            close();

        });

        rook.setOnMouseClicked(e -> {
            e.consume();

            result = 'R';
            close();

        });

        bishop.setOnMouseClicked(e -> {

            e.consume();
            result = 'B';
            close();

        });

        knight.setOnMouseClicked(e -> {

            e.consume();
            result = 'N';
            close();

        });

        Button undo = new Button("✕");
        undo.setPrefSize(squareSize, squareSize / 3.0);
        undo.setOnAction(e -> {

            e.consume();
            result = 'X';
            close();

        });

        HBox.setHgrow(undo, Priority.ALWAYS);

        VBox tileP = new VBox();
        setScene(new Scene(tileP, squareSize, squareSize * (4 + (1/3.0))));
/*         tileP.setPrefColumns(1);
        tileP.setPrefRows(5); */
        tileP.setAlignment(Pos.CENTER);
        // queen.setLayoutX(squareSize * 1);
        // rook.setLayoutX(squareSize * 2);
        // knight.setLayoutX(squareSize * 3);
        // bishop.setLayoutX(squareSize * 4);

        // double y = (squareSize - pieceSize) / 2.0;
        // queen.setLayoutY(y);
        // rook.setLayoutY(y);
        // knight.setLayoutY(y);
        // bishop.setLayoutY(y);

        if ((white && !flipped) || (!white && flipped))
            tileP.getChildren().addAll(undo, q, r, b, n);
        else
            tileP.getChildren().addAll(n, b, r, q, undo);
        tileP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        setResizable(false);

    }

}
