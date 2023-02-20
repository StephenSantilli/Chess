package game;
import java.util.ArrayList;

public class Rook extends Piece {

    public Rook(int file, int rank, boolean white) {
        super(file, rank, white);
    }

    public Rook(int file, int rank, boolean white, boolean hasMoved) {
        super(file, rank, white, hasMoved);
    }

    public String toString() {
        return "R";
    }

    public char getCode() {
        return 'R';
    }

    public int getPoints() {
        return 5;
    }

    public ArrayList<Move> getMoves(Position p) {

        ArrayList<Move> moves = new ArrayList<Move>();

        moves.addAll(getVerticalMoves(0, p, true));
        moves.addAll(getHorizontalMoves(0, p));

        return moves;

    }

}
