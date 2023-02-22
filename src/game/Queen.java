package game;

import java.util.ArrayList;

public class Queen extends Piece {

    public Queen(int file, int rank, boolean white) {
        super(file, rank, white);
    }

    public Queen(int file, int rank, boolean white, boolean hasMoved) {
        super(file, rank, white, hasMoved);
    }

    public String toString() {

        return "Q";

    }

    public char getCode() {
        return 'Q';
    }

    public int getPoints() {
        return 9;
    }

    public ArrayList<Move> getMoves(Position p) {

        ArrayList<Move> moves = new ArrayList<Move>();

        moves.addAll(getVerticalMoves(0, p, true));
        moves.addAll(getHorizontalMoves(0, p));
        moves.addAll(getDiagonalMoves(0, p));

        return moves;

    }

}
