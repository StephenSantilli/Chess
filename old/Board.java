import java.util.ArrayList;

public class Board {

    /** A list of the active pieces on the board (not captured.) */
    private ArrayList<Piece> pieces;

    /** A list of the captured pieces. */
    private ArrayList<Piece> captured;

    /** A list of all the moves made in the game, in order. */
    private ArrayList<Move> moves;

    /** If the game is over without checkmate. */
    private String gameOver;

    private int currentMove;

    public int getCurrentMove() {
        return currentMove;
    }

/*     public void undoMove(boolean deleteMove) throws Exception {


        if (moves.size() <= 1)
            return;

        if (!deleteMove) {
            if (currentMove == -1)
                currentMove = moves.size() - 2;
            else
                currentMove--;
        } else {
            currentMove = -1;
        }

        Move undo = moves.get((currentMove == -1 ? (moves.size() - 1) : (currentMove + 1)));
        if (currentMove == -1)
            moves.remove(moves.size() - 1);

        Piece survivor = getPieceAtSquare(undo.getDestination());
        survivor.setSquare(undo.getOrigin());

        if (undo.isCapture()) {

            Piece cap = captured.get(captured.size() - 1);
            captured.remove(captured.size() - 1);
            pieces.add(cap);

        }

        if (undo.isCastle() || (survivor.getCode().equals("P")
                && Math.abs(undo.getOrigin().getRank() - undo.getDestination().getRank()) == 2)) {
                    survivor.setHasMoved(false);
        }

    } */

    private Move redo;

    public void undoMove(boolean deleteMove) throws Exception {

        gameOver = null;
        Move undo = moves.get(moves.size() - 1);
        
        moves.remove(moves.size() - 1);

        initDefaultBoard();
        for(int i = 0; i < moves.size(); i++) {

            movePiece(moves.get(i));

        }

        redo = undo;

    }

    /**
     * Initializes a new board in the default position.
     */
    public Board() {

        initDefaultBoard();

        moves = new ArrayList<Move>();
        this.currentMove = -1;
        gameOver = "";
        redo = null;

    }

    /**
     * Initializes a new board, given the moves made. Assumes game starts from
     * default position.
     * 
     * @param moves A list of the moves made.
     */
    public Board(ArrayList<Move> moves) throws Exception {

        this();

        for (int i = 0; i < moves.size(); i++) {

            Move m = moves.get(i);
            movePiece(m);

        }

    }

    public Board(Board b) {
        this.pieces = b.getPieces();
        this.captured = b.getCaptured();
        this.moves = b.getMoves();
        this.gameOver = b.getGameOver();
        this.currentMove = -1;
    }

    /**
     * Initializes the default board position.
     */
    private void initDefaultBoard() {

        this.pieces = new ArrayList<Piece>();
        this.captured = new ArrayList<Piece>();

        // Kings
        pieces.add(new King(5, 1, true));
        pieces.add(new King(5, 8, false));

        boolean color = true;
        for (int i = 0; i < 2; i++) {

            for (int j = 0; j < 8; j++) {

                pieces.add(new Pawn(j + 1, color ? 2 : 7, color));

            }

            // Rooks
            pieces.add(new Rook(1, color ? 1 : 8, color));
            pieces.add(new Rook(8, color ? 1 : 8, color));

            // Queen
            pieces.add(new Queen(4, color ? 1 : 8, color));

            // Bishops
            pieces.add(new Bishop(3, color ? 1 : 8, color));
            pieces.add(new Bishop(6, color ? 1 : 8, color));

            // Knight
            pieces.add(new Knight(2, color ? 1 : 8, color));
            pieces.add(new Knight(7, color ? 1 : 8, color));

            color = false;
        }

    }

    /**
     * @return True if it is white's turn.
     */
    public boolean isWhiteTurn() {

        if (moves.isEmpty())
            return true;

        return !moves.get(moves.size() - 1).isWhite();

    }

    /**
     * @return A list of all the moves made, in order.
     */
    public ArrayList<Move> getMoves() {
        return moves;
    }

    /**
     * @return A list of all the captured pieces.
     */
    public ArrayList<Piece> getCaptured() {
        return captured;
    }

    /**
     * @return Whether or not the game is a draw.
     */
    public String getGameOver() {
        return gameOver;
    }

    /**
     * Sets if the game is a draw.
     * 
     * @param draw Whether or not the game is a draw.
     */
    public void setGameOver(String gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * @return A list of all the active pieces.
     */
    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    /**
     * Adds a piece to the board.
     * 
     * @param piece The piece to add.
     */
    public void addPiece(Piece piece) {

        pieces.add(piece);

    }

    /**
     * Removes a piece at a given square from the board. Does not add to list of
     * captured.
     * 
     * @param square The location of the piece to remove.
     * @throws Exception When there is no piece at the given square.
     */
    public void removePiece(Square square) throws Exception {

        int find = findPiece(square);
        if (find <= -1)
            throw new Exception("There is no piece there!");

        pieces.remove(findPiece(square));

    }

    /**
     * Creates a text representation of the board, along with the side that has more
     * points, if there is one.
     * 
     * @return A string representation of the board.
     */
    public String toString() {

        String s = "";

        int whitePts = 0;
        int blackPts = 0;
        for (int i = 0; i < captured.size(); i++) {
            Piece p = captured.get(i);
            if (p.isWhite())
                blackPts += p.getPoints();
            else
                whitePts += p.getPoints();

        }
        int delta = whitePts - blackPts;
        if (delta > 0)
            s += ("White +" + (delta) + "\n");
        else if (delta < 0)
            s += ("Black +" + Math.abs(delta) + "\n");

        for (int r = 8; r > 0; r--) {

            for (int c = 1; c < 9; c++) {

                Piece p = getPieceAtSquare(new Square(c, r));

                if (p == null)
                    s += "- ";
                else
                    s += (p.isWhite() ? p : p.toString().toLowerCase()) + " ";

            }

            s += "\n";

        }

        return s;

    }

    /**
     * Finds the index of the piece at a given square from the active list.
     * 
     * @param square The square to search for.
     * @return The index of the piece in the pieces array. -1 if not found.
     */
    public int findPiece(Square square) {

        int found = -1;

        for (int i = 0; i < pieces.size() && found <= -1; i++) {

            Piece p = pieces.get(i);
            if (p.getSquare().equals(square))
                found = i;

        }

        return found;

    }

    /**
     * Gets the {@link Piece} at the current square.
     * 
     * @param square The square to search for.
     * @return The {@link Piece} object at the given square. {@code null} if no
     *         piece found.
     */
    public Piece getPieceAtSquare(Square square) {

        int i = findPiece(square);

        Piece found = null;

        if (i > -1)
            found = pieces.get(i);

        return found;

    }

    /**
     * Gets a list of pieces of a certain color that can move to the given square.
     * 
     * @param square The attacking square.
     * @param white  Whether to search for white or black pieces.
     * @return A list of pieces.
     */
    public ArrayList<Piece> getPiecesByAttacking(Square square, boolean white) {

        ArrayList<Piece> found = new ArrayList<Piece>();

        for (int i = 0; i < pieces.size(); i++) {

            Piece p = pieces.get(i);
            if (p.isWhite() == white) {

                ArrayList<Move> pMoves = p.getMoves(this);
                for (int j = 0; j < pMoves.size(); j++) {

                    if (pMoves.get(j).getDestination().equals(square)) {
                        found.add(p);
                        break;
                    }

                }

            }

        }

        return found;

    }

    /**
     * Gets the king.
     * 
     * @param pieces A list of pieces to search through.
     * @param white  True if searching for white king, false if black.
     * @return The king.
     * @throws Exception If the king is not found, or the piece list is invalid.
     */
    public static Piece getKing(ArrayList<Piece> pieces, boolean white) throws Exception {

        if (pieces == null)
            throw new Exception("There are no pieces on the board!");

        if (pieces.size() < 2)
            throw new Exception("There are no kings on the board!");

        Piece k = pieces.get(white ? 0 : 1);

        if (!(k instanceof King) || k.isWhite() != white)
            throw new Exception("Invalid king position.");

        return k;

    }

    /**
     * Checks if the king is in check.
     * 
     * @param white The color of the king to search for.
     * @param b     The board position to check.
     * @return True if the king is in check.
     * @throws Exception
     */
    public static boolean inCheck(boolean white, Board b) throws Exception {

        ArrayList<Piece> pieces = b.getPieces();

        Piece k = Board.getKing(pieces, white);

        boolean check = false;

        for (int i = 0; i < pieces.size() && !check; i++) {

            Piece p = pieces.get(i);
            if (p.white == white)
                continue;

            ArrayList<Move> ms = p.getMoves(b);

            for (int j = 0; j < ms.size() && !check; j++) {

                Move m = ms.get(j);
                if (m.getDestination().equals(k.getSquare()) && m.isCapture())
                    check = true;

            }

        }

        return check;

    }

    /**
     * Checks if the given color is in a checkmate position.
     * 
     * @param white Which color pieces to check (the opposite of the king that would be mated)
     * @param b     The board to check
     * @return Whether checkmate exists
     * @throws Exception
     */
    public static boolean isMate(boolean white, Board b) throws Exception {

        ArrayList<Piece> pieces = b.getPieces();

        boolean mate = true;

        for (int i = 0; i < pieces.size() && mate; i++) {

            Piece p = pieces.get(i);
            if (p.white != white)
                continue;

            ArrayList<Move> ms = p.getMoves(b);

            for (int j = 0; j < ms.size() && mate; j++) {
                ms.get(j).setCheckAndMate(b, true);
                if (!ms.get(j).isCheck()) {
                    mate = false;
                }

            }

        }

        return mate;

    }

    /**
     * Moves a piece, and handles if it is capturing another piece.
     * 
     * @param current The current sqaure the piece is located at.
     * @param moveTo  The square to move the piece to.
     * @throws Exception If there is no piece at the given location.
     */
    public void movePiece(Move move) throws Exception {

        redo = null;


        int occupant = findPiece(move.getOrigin());

        Piece p = pieces.get(occupant);
        int moveToPiece = findPiece(move.getDestination());

        ArrayList<Move> pMoves = p.getMoves(this);
        Move valid = null;

        // Checks move against list of valid moves
        for (int i = 0; i < pMoves.size() && valid == null; i++) {
            if (pMoves.get(i).getDestination().equals(move.getDestination()))
                valid = pMoves.get(i);
        }

        if (valid != null) {

            valid.setCheckAndMate(this, true);
            valid.setText(this);

            Piece rook = move.getRook();

            Square dest = valid.getDestination();

            // Promotion
            if (p instanceof Pawn
                    && ((dest.getRank() == 8 && p.isWhite()) || (dest.getRank() == 1 && !p.isWhite()))) {

                Piece promo = null;
                switch (move.getPromoteType()) {

                    case 'Q':
                        promo = new Queen(dest.getFile(), dest.getRank(), p.isWhite());
                        break;
                    case 'N':
                        promo = new Knight(dest.getFile(), dest.getRank(), p.isWhite());
                        break;
                    case 'B':
                        promo = new Bishop(dest.getFile(), dest.getRank(), p.isWhite());
                        break;
                    case 'R':
                        promo = new Rook(dest.getFile(), dest.getRank(), p.isWhite());
                        break;
                }

                if (promo == null)
                    throw new Exception("No or invalid promotion selection.");

                removePiece(p.getSquare());
                addPiece(promo);
                promo.setHasMoved(true);

                // Castling
            } else if (p instanceof King && rook != null) {

                p.setSquare(move.getDestination());
                p.setHasMoved(true);

                if (move.getDestination().getFile() == 7) {
                    rook.setSquare(new Square(6, move.getDestination().getRank()));
                } else if (move.getDestination().getFile() == 3) {
                    rook.setSquare(new Square(4, move.getDestination().getRank()));
                }
                rook.setHasMoved(true);

                // Normal move
            } else {

                if (!valid.isCapture() && moveToPiece > -1) {
                    throw new Exception("Invalid capture.");
                }

                p.setSquare(move.getDestination());
                p.setHasMoved(true);

            }

        } else {

            String s = "Invalid move. Valid moves are: ";
            for (int i = 0; i < pMoves.size(); i++) {
                s += pMoves.get(i).getMoveText() + (i + 1 < pMoves.size() ? ", " : ".");
            }

            throw new Exception(s);

        }

        // En passant
        if (valid.isEnPassant())
            moveToPiece = findPiece(
                    new Square(valid.getDestination().getFile(),
                            (p.isWhite() ? valid.getDestination().getRank() - 1
                                    : valid.getDestination().getRank() + 1)));

        // If capture
        if (moveToPiece > -1 && valid.isCapture()) {

            captured.add(pieces.get(moveToPiece));
            pieces.remove(moveToPiece);

        }

        moves.add(valid);

    }

}
