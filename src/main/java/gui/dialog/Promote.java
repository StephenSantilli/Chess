package gui.dialog;

import game.pieces.Bishop;
import game.pieces.Knight;
import game.pieces.Queen;
import game.pieces.Rook;
import gui.GameView;
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

/**
 * The dialog that is displayed to users when they must choose which piece they
 * want to promote their pawn to.
 */
public class Promote extends Stage {

    /**
     * The piece code the the user selected. Will be {@code X} if the user chose not
     * to promote. Will also be {@code X} before the user has made a choice.
     */
    private char result;

    /**
     * Creates a new promote dialog which will be shown to the user so they can
     * select which piece to promote to.
     * 
     * @param gameView The GameView that created this dialog.
     * @param white    Whether or not the pieces should be white.
     * @param owner    The owner of this dialog.
     */
    public Promote(GameView gameView, boolean white, Window owner) {

        this.result = 'X';

        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);

        initStyle(StageStyle.UNDECORATED);

        ImageView queen = gameView.getBoard().getPiecePane().getPieceTranscoder(new Queen(0, 0, white)).toImageView();
        ImageView rook = gameView.getBoard().getPiecePane().getPieceTranscoder(new Rook(0, 0, white)).toImageView();
        ImageView bishop = gameView.getBoard().getPiecePane().getPieceTranscoder(new Bishop(0, 0, white)).toImageView();
        ImageView knight = gameView.getBoard().getPiecePane().getPieceTranscoder(new Knight(0, 0, white)).toImageView();

        final double pieceSize = gameView.getBoard().getPieceSize();
        final double squareSize = gameView.getBoard().getSquareSize();
        final boolean flipped = gameView.isFlipped();

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
        tileP.setAlignment(Pos.CENTER);

        setScene(new Scene(tileP, squareSize, squareSize * (4 + (1 / 3.0))));

        if ((white && !flipped) || (!white && flipped))
            tileP.getChildren().addAll(undo, q, r, b, n);
        else
            tileP.getChildren().addAll(n, b, r, q, undo);

        tileP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        setResizable(false);

    }

    /**
     * Gets the result of the user's selection on the dialog.
     * 
     * @return {@link #result}
     */
    public char getResult() {
        return result;
    }

}