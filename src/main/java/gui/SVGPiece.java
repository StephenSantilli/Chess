package gui;

import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

public class SVGPiece extends Pane {
    
    private int pieceSize;
    private boolean color;
    private char pieceType;
    

    public int getPieceSize() {
        return pieceSize;
    }


    public boolean isColor() {
        return color;
    }


    public char getPieceType() {
        return pieceType;
    }


    public SVGPiece(int pieceSize, boolean color, char pieceType) {
        SVGPath img = new SVGPath();
        img.setContent("M 17.5,26 L 27.5,26 M 15,30 L 30,30 M 22.5,15.5 L 22.5,20.5 M 20,18 L 25,18");
        getChildren().add(img);

    }

}
