package game.pieces;

import java.util.ArrayList;

import game.Move;
import game.Position;

public class Queen extends Piece {

    public Queen(int file, int rank, boolean white) {
        super(file, rank, white);
    }

    public Queen(int file, int rank, boolean white, boolean hasMoved) {
        super(file, rank, white, hasMoved);
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
