import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(int file, int rank, boolean white) {
        super(file, rank, white);
    }

    public Bishop(int file, int rank, boolean white, boolean hasMoved) {
        super(file, rank, white, hasMoved);
    }

    public String toString() {

        return "B";

    }

    public char getCode() {
        return 'B';
    }

    public int getPoints() {
        return 3;
    }

    public ArrayList<Move> getMoves(Position p) {

        ArrayList<Move> moves = new ArrayList<Move>();

        moves.addAll(getDiagonalMoves(0, p));

        return moves;

    }
    
}
