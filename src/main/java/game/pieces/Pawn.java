package game.pieces;

import java.util.ArrayList;

import game.Move;
import game.Position;
import game.Square;

public class Pawn extends Piece {

    public Pawn(int file, int rank, boolean white) {
        super(file, rank, white);
    }

    public Pawn(int file, int rank, boolean white, boolean hasMoved) {
        super(file, rank, white, hasMoved);
    }

    public char getCode() {
        return 'P';
    }

    public int getPoints() {
        return 1;
    }

    public ArrayList<Move> getMoves(Position p) {

        ArrayList<Move> moves = new ArrayList<Move>();

        int file = square.getFile();
        int rank = square.getRank();

        // Move forward 1 or 2 spaces
        if (!hasMoved)
            moves.addAll(getVerticalMoves(2, p, false));
        else
            moves.addAll(getVerticalMoves(1, p, false));

        // Capture left
        try {
            Move move = new Move(square, new Square(file - 1, white ? rank + 1 : rank - 1), p);
            moves.add(move);
        } catch (Exception e) {
        }

        // Capture right
        try {
            Move move = new Move(square, new Square(file + 1, white ? rank + 1 : rank - 1), p);
            moves.add(move);
        } catch (Exception e) {
        }

        return moves;

    }

}
