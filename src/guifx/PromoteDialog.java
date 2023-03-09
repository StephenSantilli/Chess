package guifx;

import game.Game;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;

public class PromoteDialog extends Dialog<Character> {

    private Game game;

    public PromoteDialog(Game game, int pieceSize) throws Exception {

        initModality(Modality.NONE);

        this.game = game;
        boolean white = !game.getActivePos().isWhite();
        ImageView queen = new PieceTranscoder(pieceSize, white, 'Q').getImageView();
        ImageView rook = new PieceTranscoder(pieceSize, white, 'R').getImageView();
        ImageView bishop = new PieceTranscoder(pieceSize, white, 'B').getImageView();
        ImageView knight = new PieceTranscoder(pieceSize, white, 'N').getImageView();

        Button q = new Button("", queen);
        queen.setOnMouseClicked(e -> {

            selection('Q');
            close();

        });

        Button r = new Button("", rook);
        rook.setOnMouseClicked(e -> {

            selection('R');
            close();

        });

        Button b = new Button("", bishop);
        bishop.setOnMouseClicked(e -> {

            selection('B');
            close();

        });

        Button n = new Button("", knight);
        knight.setOnMouseClicked(e -> {

            selection('N');
            close();

        });

        Button undo = new Button("✕");
        undo.setOnAction(e -> {

            selection('X');
            close();

        });

        HBox hbox = new HBox(queen, rook, knight, bishop, undo);
        getDialogPane().setContent(hbox);
        

    }

    private void selection(char piece) {

        setResult(piece);

    }

}
