package game;
import java.util.ArrayList;

public class King extends Piece {

    public King(int file, int rank, boolean white) {
        super(file, rank, white);
    }
    
    public King(int file, int rank, boolean white, boolean hasMoved) {
        super(file, rank, white, hasMoved);
    }


    public String toString() {

        return "K";

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
        Piece kingRook = p.getPieceAtSquare(new Square(8, square.getRank()));
        Piece queenRook = p.getPieceAtSquare(new Square(1, square.getRank()));

        if (!hasMoved) {

            if (kingRook != null && !kingRook.hasMoved()) {

                boolean canReach = true;
                for (int i = 6; i < 8 && canReach; i++) {

                    if (p.getPieceAtSquare(new Square(i, square.getRank())) != null)
                        canReach = false;

                }

                if (canReach) {
                    try {
                        moves.add(new Move(square, new Square(7, white ? 1 : 8), p));
                    } catch (Exception e) {
                    }
                }

            }

            if (queenRook != null && !queenRook.hasMoved()) {

                boolean canReach = true;
                for (int i = 4; i > 1 && canReach; i--) {

                    if (p.getPieceAtSquare(new Square(i, square.getRank())) != null)
                        canReach = false;

                }

                if (canReach) {
                    try {
                        moves.add(new Move(square, new Square(3, white ? 1 : 8), p));
                    } catch (Exception e) {
                    }
                }

            }

        }

        return moves;

    }

}
