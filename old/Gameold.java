import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Gameold {

    private Board board;

    public Gameold() {

        this.board = new Board();

    }

    private String isGameOver() throws Exception {

        if (Board.isMate(true, board)) {
            return "White wins by checkmate.";
        } else if (Board.isMate(false, board)) {
            return "Black wins by checkmate.";
        } else {
            return "Non checkmate: " + board.getGameOver();
        }

        // ADD DRAW

    }

    public String outputMoves() throws Exception {

        ArrayList<Move> moves = board.getMoves();
        String s = "";
        for (int i = 0; i < moves.size(); i += 2) {

            s += (i / 2 + 1) + ". " + moves.get(i).getMoveText() + " ";

            if (i + 1 < moves.size())
                s += moves.get(i + 1).getMoveText();

            s += " ";

        }

        return s;

    }

    /**
     * @return True if it is white's turn.
     */
    public boolean isWhiteTurn() {

        ArrayList<Move> moves = board.getMoves();

        if (moves.size() == 0)
            return true;

        return !moves.get(moves.size() - 1).isWhite();

    }

    public void importPos(String filename) throws Exception {

        Scanner file = new Scanner(new File(filename + ".pgn"));

        String str = "";
        boolean started = false;
        while (file.hasNextLine()) {

            String line = file.nextLine();

            if (started) {
                str += " " + line;
                continue;
            }

            int moveStart = line.indexOf("1. ");
            if (moveStart >= 0) {
                started = true;
                str += line.substring(moveStart, line.length());
            }

        }
        file.close();

        int i = 1;
        int index = str.indexOf("1.");
        while (index != -1) {

            ++i;
            int nextIndex = str.indexOf(i + ".");
            String[] line;

            try {
                line = str.substring(index, nextIndex == -1 ? str.length() : nextIndex).split("[ \n]+");
            } catch (Exception e) {
                System.out.println(str.substring(index));
                System.out.println(e);
                return;
            }

            if (line[1].matches("[0][-][1]|[1][-][0]|[1/2][-][1/2]")) {
                break;
            }

            Move white = new Move(line[1], board, true);
            try {
                board.movePiece(white);
            } catch (Exception e) {
                System.out.println("Import failed @ line " + i + ": " + e.getMessage());
                return;
            }

            if (line[2].matches("[0][-][1]|[1][-][0]|[1/2][-][1/2]")) {
                break;
            }

            if (line.length >= 3) {
                Move black = new Move(line[2], board, false);
                try {
                    board.movePiece(black);
                } catch (Exception e) {
                    System.out.println("Import failed @ line " + i + ": " + e.getMessage());
                    return;
                }
            }

            index = nextIndex;

        }

        if (str.indexOf("1/2-1/2") > -1) {
            board.setGameOver("1/2-1/2");
        }

        if (str.indexOf("1-0") > -1) {
            board.setGameOver("1-0");
        }

        if (str.indexOf("0-1") > -1) {
            board.setGameOver("0-1");
        }

    }

    public void makeMove(String move) throws Exception {

        if (move.startsWith("import")) {

            importPos(move.split(" ")[1]);

            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            System.out.println(board);
            System.out.println(isGameOver());
            System.out.println("Moves imported!");

        } else {

            if (move.equals("restart")) {

                board = new Board();
                return;

            }

            if (move.equals("dupe")) {
                try {
                    Board b = new Board(board.getMoves());
                    System.out.println();
                    System.out.println(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;

            }

            try {
                board.movePiece(new Move(move, board, isWhiteTurn()));
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                System.out.println(board);
                System.out.println(isGameOver());
                System.out.println("Move successful!");
            } catch (Exception e) {

                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                System.out.println(board);
                System.out.println(e);
                // e.printStackTrace();

            }
        }

    }

    public void getMoves(String input) throws Exception {

        int fFile = input.charAt(0) - 96;
        int fRank = Integer.parseInt(input.substring(1));
        Square fSquare = new Square(fFile, fRank);
        ArrayList<Move> moves = board.getPieceAtSquare(fSquare).getMoves(board);
        for (int i = 0; i < moves.size(); i++) {
            System.out.println(moves.get(i));
        }

    }

    public Board getBoard() {
        return board;
    }

    /*
     * public static void main(String[] args) throws Exception {
     * 
     * Game g = new Game();
     * 
     * Scanner scanner = new Scanner(new InputStreamReader(System.in));
     * 
     * System.out.println(g.getBoard());
     * 
     * while (!Board.isMate(true, g.getBoard()) && !Board.isMate(false,
     * g.getBoard())) {
     * 
     * g.makeMove(scanner.nextLine());
     * 
     * }
     * 
     * System.out.println("Checkmate!");
     * 
     * scanner.close();
     * 
     * }
     */

}
