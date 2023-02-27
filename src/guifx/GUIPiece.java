package guifx;

import game.Piece;
import javafx.scene.image.ImageView;

public class GUIPiece {
    
    private Piece piece;
    private ImageView image;

    public GUIPiece(Piece piece, ImageView image) {

        this.piece = piece;
        this.image = image;

    }

    public Piece getPiece() {
        return piece;
    }

    public ImageView getImage() {
        return image;
    }

}
