package game.pieces;

import java.util.ArrayList;

import game.Move;
import game.Position;
import game.Square;

public class King extends Piece {

    public King(int file, int rank, boolean white) {
        super(file, rank, white);
    }

    public King(int file, int rank, boolean white, boolean hasMoved) {
        super(file, rank, white, hasMoved);
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
        // Piece kingRook = p.getPieceAtSquare(new Square(8, square.getRank()));
        // Piece queenRook = p.getPieceAtSquare(new Square(1, square.getRank()));

        Piece hRook = p.getRook(false, white), aRook = p.getRook(true, white);

        if (!hasMoved) {

            if (hRook != null && !hRook.hasMoved()) {

                boolean canReach = true;

                // If king can reach
                for (int i = square.getFile() + 1; i < 8 && canReach; i++) {

                    Piece find = p.getPieceAtSquare(new Square(i, square.getRank()));
                    if (find != null && find.getCode() != 'R')
                        canReach = false;

                }

                // If rook can reach
                for (int i = hRook.getSquare().getFile() - 1; i >= 6 && canReach; i--) {

                    Piece find = p.getPieceAtSquare(new Square(i, hRook.getSquare().getRank()));
                    if (find != null && find.getCode() != 'K')
                        canReach = false;

                }

                if (canReach) {
                    try {
                        moves.add(new Move(square, new Square(7, white ? 1 : 8), p, true));
                    } catch (Exception e) {
                    }
                }

            }

            if (aRook != null && !aRook.hasMoved()) {

                boolean canReach = true;

                // If king can reach
                for (int i = square.getFile() - 1; i > 2 && canReach; i--) {
                    Piece find = p.getPieceAtSquare(new Square(i, square.getRank()));
                    if (find != null && find.getCode() != 'R')
                        canReach = false;

                }

                // If rook can reach
                for (int i = aRook.getSquare().getFile() + 1; i <= 4 && canReach; i++) {

                    Piece find = p.getPieceAtSquare(new Square(i, aRook.getSquare().getRank()));
                    if (find != null && find.getCode() != 'K')
                        canReach = false;

                }

                if (canReach) {
                    try {
                        moves.add(new Move(square, new Square(3, white ? 1 : 8), p, true));
                    } catch (Exception e) {
                    }
                }

            }

        }

        return moves;

    }

}
