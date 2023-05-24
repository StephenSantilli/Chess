package game.pieces;

import java.util.ArrayList;

import game.Move;
import game.Position;
import game.Square;

/**
 * A representation of a king.
 */
public class King extends Piece {

    /**
     * Creates a new King object.
     * 
     * @param file  The file (column) the piece is on.
     * @param rank  The rank (row) the piece is on.
     * @param white Whether the piece is white or not. (True if white, false if
     *              black)
     */
    public King(int file, int rank, boolean white) {
        super(file, rank, white);
    }

    public char getCode() {
        return 'K';
    }

    public int getPoints() {
        return 0;
    }

    public ArrayList<Move> getMoves(Position p) {

        ArrayList<Move> moves = new ArrayList<Move>();

        moves.addAll(getVerticalMoves(1, p, true));
        moves.addAll(getHorizontalMoves(1, p));
        moves.addAll(getDiagonalMoves(1, p));

        // Castling
        Piece hRook = p.getRook(false, white), aRook = p.getRook(true, white);

        if (hRook != null && p.canCastle(white, false)) {

            boolean canReach = true;

            // If king can reach
            boolean left = square.getFile() > 7;

            for (int inc = (left ? -1 : 1), i = square.getFile() + inc; canReach
                    && ((left && i >= 7) || (!left && i <= 7)); i += inc) {

                if (i == p.gethSideRookFile())
                    continue;

                Piece find = p.getPieceAtSquare(new Square(i, square.getRank()));
                if (find != null)
                    canReach = false;

            }

            // If rook can reach
            left = hRook.getSquare().getFile() > 6;

            for (int inc = (left ? -1 : 1), i = hRook.getSquare().getFile() + inc; canReach
                    && ((left && i >= 6) || (!left && i <= 6)); i += inc) {

                if (i == square.getFile())
                    continue;

                Piece find = p.getPieceAtSquare(new Square(i, square.getRank()));
                if (find != null)
                    canReach = false;

            }

            if (canReach) {

                try {
                    moves.add(new Move(square, new Square(7, white ? 1 : 8), p, true));
                } catch (Exception e) {
                }

            }

        }

        if (aRook != null && p.canCastle(white, true)) {

            boolean canReach = true;

            // If king can reach
            boolean left = square.getFile() > 3;

            for (int inc = (left ? -1 : 1), i = square.getFile() + inc; canReach
                    && ((left && i >= 3) || (!left && i <= 3)); i += inc) {

                if (i == p.gethSideRookFile())
                    continue;

                Piece find = p.getPieceAtSquare(new Square(i, square.getRank()));
                if (find != null)
                    canReach = false;

            }

            // If rook can reach
            left = aRook.getSquare().getFile() > 4;

            for (int inc = (left ? -1 : 1), i = aRook.getSquare().getFile() + inc; canReach
                    && ((left && i >= 4) || (!left && i <= 4)); i += inc) {

                if (i == square.getFile())
                    continue;

                Piece find = p.getPieceAtSquare(new Square(i, square.getRank()));
                if (find != null)
                    canReach = false;

            }

            if (canReach) {

                try {
                    moves.add(new Move(square, new Square(3, white ? 1 : 8), p, true));
                } catch (Exception e) {
                }

            }

        }

        return moves;

    }

}
