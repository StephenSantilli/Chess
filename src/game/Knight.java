package game;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(int file, int rank, boolean white) {
        super(file, rank, white);
    }

    public Knight(int file, int rank, boolean white, boolean hasMoved) {
        super(file, rank, white, hasMoved);
    }

    public String toString() {

        return "N";

    }

    public char getCode() {
        return 'N';
    }

    public int getPoints() {
        return 3;
    }

    public ArrayList<Move> getMoves(Position p) {

        ArrayList<Move> moves = new ArrayList<Move>();

        int file = square.getFile();
        int rank = square.getRank();

        for (int i = 0; i < 4; i++) {

            int one = 1, two = 2;

            switch (i) {
                case 1:
                    one = -1;
                    break;
                case 2:
                    two = -2;
                    break;
                case 3:
                    one = -1;
                    two = -2;
                    break;
            }

            // Vertical one & horizontal two
            try {
                Move move = new Move(square, new Square(file + one, rank + two), p);
                moves.add(move);
            } catch (Exception e) {
            }

            // Vertical two & horizontal one
            try {
                Move move = new Move(square, new Square(file + two, rank + one), p);
                moves.add(move);
            } catch (Exception e) {
            }

        }

        return moves;

    }

}
