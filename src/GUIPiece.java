import javax.swing.ImageIcon;

public class GUIPiece {

    private ImageIcon image;

    public ImageIcon getImage() {
        return image;
    }

    private Piece piece;

    public Piece getPiece() {
        return piece;
    }

    private int x, y;

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public GUIPiece(ImageIcon image, Piece piece, int x, int y) {
        this.image = image;
        this.piece = piece;
        this.x = x;
        this.y = y;
    }

}
