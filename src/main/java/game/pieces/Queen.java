package game.pieces;

import java.util.ArrayList;

import game.Move;
import game.Position;

public class Queen extends Piece {
    
    /**
     * Creates a new Queen object.
     * 
     * @param file  The file (column) the piece is on.
     * @param rank  The rank (row) the piece is on.
     * @param white Whether the piece is white or not. (True if white, false if
     *              black)
     */
    public Queen(int file, int rank, boolean white) {
        super(file, rank, white);
    }

    /**
     * Creates a new Queen object.
     * 
     * @param file     The file (column) the piece is on.
     * @param rank     The rank (row) the piece is on.
     * @param white    Whether the piece is white or not. (True if white, false if
     *                 black)
     * @param hasMoved Whether or not the piece has moved.
     */
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
