package game.pieces;

import java.util.ArrayList;

import game.Move;
import game.Position;

public class Bishop extends Piece {

    public Bishop(int file, int rank, boolean white) {
        super(file, rank, white);
    }

    public Bishop(int file, int rank, boolean white, boolean hasMoved) {
        super(file, rank, white, hasMoved);
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
