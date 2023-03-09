package guifx;

import game.Game;
import game.Move;
import game.Piece;
import game.Position;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
        q.setOnAction(e -> {
            
            selection('Q');
            close();

        });

        Button r = new Button("", rook);
        r.setOnAction(e -> {

            selection('R');
            close();

        });

        Button b = new Button("", bishop);
        b.setOnAction(e -> {

            selection('B');
            close();

        });

        Button n = new Button("", knight);
        n.setOnAction(e -> {

            selection('N');
            close();

        });


        HBox hbox = new HBox(q, r, b, n);

        getDialogPane().setContent(hbox);

    }

    private void selection(char piece) {

        setResult(piece);

    }

}
