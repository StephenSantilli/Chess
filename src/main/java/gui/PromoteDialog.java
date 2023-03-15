package gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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

        this.result = 'X';

        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);

        initStyle(StageStyle.UNDECORATED);

        SVGPiece q = new SVGPiece(pieceSize, white, 'Q');
        q.setPrefHeight(squareSize);
        q.setPrefWidth(squareSize);
        q.setLayoutX((squareSize - pieceSize) / 2.0);
        q.setLayoutY((squareSize - pieceSize) / 2.0);

        SVGPiece r = new SVGPiece(pieceSize, white, 'R');
        r.setPrefHeight(squareSize);
        r.setPrefWidth(squareSize);
        r.setLayoutX((squareSize - pieceSize) / 2.0);
        r.setLayoutY((squareSize - pieceSize) / 2.0);

        SVGPiece b = new SVGPiece(pieceSize, white, 'B');
        b.setPrefHeight(squareSize);
        b.setPrefWidth(squareSize);
        b.setLayoutX((squareSize - pieceSize) / 2.0);
        b.setLayoutY((squareSize - pieceSize) / 2.0);

        SVGPiece n = new SVGPiece(pieceSize, white, 'N');
        n.setPrefHeight(squareSize);
        n.setPrefWidth(squareSize);
        n.setLayoutX((squareSize - pieceSize) / 2.0);
        n.setLayoutY((squareSize - pieceSize) / 2.0);

        q.setOnMouseClicked(e -> {

            e.consume();
            result = 'Q';
            close();

        });

        r.setOnMouseClicked(e -> {

            e.consume();
            result = 'R';
            close();

        });

        b.setOnMouseClicked(e -> {

            e.consume();
            result = 'B';
            close();

        });

        n.setOnMouseClicked(e -> {

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
        tileP.setAlignment(Pos.CENTER);

        setScene(new Scene(tileP, squareSize, squareSize * (4 + (1 / 3.0))));

        if ((white && !flipped) || (!white && flipped))
            tileP.getChildren().addAll(undo, q, r, b, n);
        else
            tileP.getChildren().addAll(n, b, r, q, undo);

        tileP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        setResizable(false);

    }

}
