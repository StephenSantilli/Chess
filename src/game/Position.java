package game;

import java.util.ArrayList;

public class Position {

    /** All of the pieces in the position. Does not include captured pieces. */
    private ArrayList<Piece> pieces;

    /**
     * All of the moves that can be made. If {@code checkForMate} is {@code true}
     * when constructor is called, moves that lead to check will not be included.
     */
    private ArrayList<Move> moves;

    /** The move that led to this position. */
    private Move move;

    /** The position of the white king. */
    private Square whiteKing;

    /** The position of the black king. */
    private Square blackKing;

    /**
     * Whether or not it is currently white's turn, not whether white made the move
     * that led to this position. Will be the opposite of {@link move#isWhite()};
     */
    private boolean white;

    /** If {@link #isWhite()} is currently giving check. */
    private boolean givingCheck;

    /** If {@link #isWhite()} is currently in check. */
    private boolean inCheck;

    /**
     * If {@link #isWhite()} is checkmated. Will only be set if {@code checkForMate}
     * is {@code true} when constructor is called.
     */
    private boolean checkMate;

    /**
     * The {@link Position} that was previously after this {@link Position}. May be
     * {@code null} if there is no position to redo.
     */
    private Position redo;

    /**
     * @return The {@link Position} that was previously after this {@link Position}.
     */
    public Position getRedo() {
        return redo;
    }

    /**
     * Returns a string representation of the move that led to this position.
     * 
     * @return A {@link String} containing the move text.
     */
    public String getMoveString() {

        String str = move.getMoveText();
        if (isCheckMate())
            str += "#";
        else if (isInCheck())
            str += "+";

        return str;

    }

    /**
     * Sets the redo {@link Position}.
     * 
     * @param redo The redo {@link Position}
     */
    public void setRedo(Position redo) {
        this.redo = redo;
    }

    /**
     * @return A list of the {@link Piece} objects in this position. Does not
     *         include captured pieces.
     */
    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    /**
     * @return A list of the {@link Move} objects possible in this position. If
     *         {@code checkForMate} is {@code true} when constructor is called,
     *         moves that lead to check will not be included.
     */
    public ArrayList<Move> getMoves() {
        return moves;
    }

    /**
     * @return Gets the current {@link Move} that led to this position.
     */
    public Move getMove() {
        return move;
    }

    /**
     * @return Returns {@code true} if it is currently white's turn. Is the opposite
     *         color of the move that led to this position.
     */
    public boolean isWhite() {
        return white;
    }

    /**
     * @return Returns {@code true} if the value of {@link #isWhite()} is giving
     *         check to the other color.
     */
    public boolean isGivingCheck() {
        return givingCheck;
    }

    /**
     * @return Returns {@code true} if the value of {@link #isWhite()} is in check
     *         from the other color.
     */
    public boolean isInCheck() {
        return inCheck;
    }

    /**
     * @return Returns {@code true} if the current position is check mate. May be
     *         {@code null} if {@code checkForMate} was not true when constructor
     *         was called.
     */
    public boolean isCheckMate() {
        return checkMate;
    }

    /**
     * Creates a new {@link Position} object in the default starting position.
     * 
     * @param game The game this position is associated with.
     */
    public Position(Game game) {

        white = true;
        initDefaultPosition();
        initMoves(true, game);

    }

    /**
     * Creates a new {@link Position} object from the previous position with the new
     * {@link Move}.
     * 
     * @param prev         The previous position to use as a baseline for this
     *                     position.
     * @param move         The move to be made.
     * @param game         The game this position is associated with.
     * @param isWhite      Whether or not it is white's turn after this move is
     *                     made.
     * @param checkForMate Whether or not checkmate should be checked for.
     */
    public Position(Position prev, Move move, Game game, boolean isWhite, boolean checkForMate) {

        this.pieces = new ArrayList<Piece>();
        this.white = isWhite;

        ArrayList<Piece> prevPieces = prev.getPieces();

        for (int i = 0; i < prevPieces.size(); i++) {

            Piece old = prevPieces.get(i);
            Piece piece = null;
            switch (old.getCode()) {
                case 'P':
                    piece = new Pawn(old.getSquare().getFile(), old.getSquare().getRank(), old.isWhite(),
                            old.hasMoved());
                    break;
                case 'K':
                    piece = new King(old.getSquare().getFile(), old.getSquare().getRank(), old.isWhite(),
                            old.hasMoved());
                    if (piece.isWhite())
                        whiteKing = piece.getSquare();
                    else
                        blackKing = piece.getSquare();

                    break;
                case 'N':
                    piece = new Knight(old.getSquare().getFile(), old.getSquare().getRank(), old.isWhite(),
                            old.hasMoved());
                    break;
                case 'Q':
                    piece = new Queen(old.getSquare().getFile(), old.getSquare().getRank(), old.isWhite(),
                            old.hasMoved());
                    break;
                case 'B':
                    piece = new Bishop(old.getSquare().getFile(), old.getSquare().getRank(), old.isWhite(),
                            old.hasMoved());
                    break;
                case 'R':
                    piece = new Rook(old.getSquare().getFile(), old.getSquare().getRank(), old.isWhite(),
                            old.hasMoved());
                    break;
            }

            if (piece != null)
                pieces.add(piece);

        }

        if (move.isCapture()) {

            int capIndex = findPiece(move.getCaptureSquare());
            pieces.remove(capIndex);

        }

        int movePIndex = findPiece(move.getOrigin());
        Piece movePiece = pieces.get(movePIndex);

        if (movePiece.getCode() == 'K') {
            if (movePiece.isWhite())
                whiteKing = move.getDestination();
            else
                blackKing = move.getDestination();
        }

        movePiece.setSquare(move.getDestination());
        movePiece.setHasMoved(true);

        if (move.isCastle()) {

            Piece rook = getPieceAtSquare(move.getRookLocation());
            rook.setSquare(new Square(move.getDestination().getFile() == 7 ? 6 : 4, rook.getSquare().getRank()));

        }

        if (checkForMate && move.getPromoteType() == '?') {

            char promoRes = game.firePromptForPromote(move);

            if (promoRes == 'Q' || promoRes == 'R' || promoRes == 'B' || promoRes == 'N') {
                move.setPromoteType(promoRes);

                pieces.remove(movePIndex);
                Square mps = movePiece.getSquare();

                switch (promoRes) {
                    case 'Q':
                        pieces.add(new Queen(mps.getFile(), mps.getRank(), movePiece.isWhite()));
                        break;
                    case 'R':
                        pieces.add(new Rook(mps.getFile(), mps.getRank(), movePiece.isWhite()));
                        break;
                    case 'B':
                        pieces.add(new Bishop(mps.getFile(), mps.getRank(), movePiece.isWhite()));
                        break;
                    case 'N':
                        pieces.add(new Knight(mps.getFile(), mps.getRank(), movePiece.isWhite()));
                        break;
                }

            }

        }

        initMoves(checkForMate, game);

        move.setText(this);
        this.move = move;

    }

    /**
     * Initializes the list of moves.
     * 
     * @param checkForMate
     * @param g
     */
    private void initMoves(boolean checkForMate, Game g) {

        this.moves = new ArrayList<Move>();

        for (int i = 0; i < pieces.size(); i++) {

            moves.addAll(pieces.get(i).getMoves(this));

        }

        ArrayList<Piece> ownPieces = getPiecesByAttacking(white ? whiteKing : blackKing);
        if (ownPieces.size() >= 1)
            inCheck = true;

        ArrayList<Piece> oppPieces = getPiecesByAttacking(!white ? whiteKing : blackKing);
        if (oppPieces.size() >= 1)
            givingCheck = true;

        if (checkForMate) {
            setCheckMate(g);
        }

    }

    /**
     * Calculates the amount of points the given color has.
     * 
     * @param white True if checking for white's points
     * @return The point delta of the color chosen. Will be 0 if color has less
     *         material.
     */
    public int getCapturedPoints(boolean white) {

        int whitePoints = 0;
        int blackPoints = 0;

        for (int i = 0; i < pieces.size(); i++) {

            Piece p = pieces.get(i);

            if (p.isWhite())
                whitePoints += p.getPoints();
            else
                blackPoints += p.getPoints();

        }

        if (white && blackPoints > whitePoints || !white && whitePoints > blackPoints)
            return 0;

        if (white)
            return whitePoints - blackPoints;
        else
            return blackPoints - whitePoints;

    }

    /**
     * Sets whether or not the position is check mate.
     * 
     * @param g The game to check.
     */
    private void setCheckMate(Game g) {

        /*
         * if (!givingCheck) {
         * this.checkMate = false;
         * return;
         * }
         */

        this.checkMate = true;

        for (int i = 0; i < moves.size(); i++) {

            Move m = moves.get(i);

            if (m.isWhite() != isWhite()) {
                moves.remove(i);
                --i;
                continue;
            }

            Position test = new Position(this, m, g, !white, false);
            if (!test.isGivingCheck())
                checkMate = false;
            else {
                moves.remove(i);
                --i;
            }

        }

    }

    /**
     * Initializes the default board pieces and their positions.
     */
    private void initDefaultPosition() {

        this.pieces = new ArrayList<Piece>();

        boolean color = true;
        for (int i = 0; i < 2; i++) {

            for (int j = 0; j < 8; j++) {

                pieces.add(new Pawn(j + 1, color ? 2 : 7, color));

            }

            // Kings
            Piece king = new King(5, color ? 1 : 8, color);
            if (color)
                whiteKing = king.getSquare();
            else
                blackKing = king.getSquare();
            pieces.add(king);

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
     * Finds the first occurance of a piece at the given square.
     * 
     * @param s The square to search for a piece at
     * @return The index of the piece in the {@link #pieces} array. Will be
     *         {@code -1} if
     *         no piece is at the square.
     */
    public int findPiece(Square s) {

        int found = -1;
        for (int i = 0; i < pieces.size() && found <= -1; i++) {

            if (pieces.get(i).getSquare().equals(s))
                found = i;

        }

        return found;

    }

    /**
     * Finds the first occurance of a piece at the given square.
     * 
     * @param s The square to search for a piece at
     * @return The {@link Piece} object. Will be {@code null}
     *         if no
     *         piece is at the square.
     */
    public Piece getPieceAtSquare(Square s) {

        int index = findPiece(s);

        Piece piece = null;
        if (index >= 0)
            piece = pieces.get(index);

        return piece;

    }

    /**
     * Gets a list of the pieces that are attacking the given square.
     * 
     * @param s The square to search for attacking pieces at.
     * @return An {@link ArrayList} of {@link Piece} objects
     */
    public ArrayList<Piece> getPiecesByAttacking(Square s) {

        ArrayList<Piece> pieces = new ArrayList<Piece>();

        for (int i = 0; i < moves.size(); i++) {

            Move m = moves.get(i);
            if (m.isCapture() && m.getCaptureSquare().equals(s))
                pieces.add(m.getPiece());

        }

        return pieces;

    }

    /**
     * Gets the square that the king resides on.
     * 
     * @param white The color of the piece to search for. True if white.
     * @return A {@link Square} object
     */
    public Square getKingSquare(boolean white) {

        return white ? whiteKing : blackKing;

    }

    /**
     * Tests to see if the current board position is check.
     * 
     * @param white
     * @return
     */
    @Deprecated
    public boolean isCheck(boolean white) {

        return getPiecesByAttacking(getKingSquare(white)).size() > 0;

    }

}
