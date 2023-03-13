package game;

import java.util.ArrayList;

public class Position {

    private Piece[][] pieces;

    /** All of the pieces in the position. Does not include captured pieces. */
    public Piece[][] getPieces() {
        return pieces;
    }

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
     * The promote type of the redo. May be {@code null} if there is no position to
     * redo.
     */
    private char redoPromote;

    /**
     * The system time at the start of this position.
     */
    private long systemTimeStart;

    /**
     * The amount of time on the timer at the end of this position.
     */
    private long timerEnd;

    public long getSystemTimeStart() {
        return systemTimeStart;
    }

    public void setSystemTimeStart(long timerStart) {
        this.systemTimeStart = timerStart;
    }

    public long getTimerEnd() {
        return timerEnd;
    }

    public void setTimerEnd(long timerEnd) {
        this.timerEnd = timerEnd;
    }

    /**
     * @return The {@link Position} that was previously after this {@link Position}.
     */
    public Position getRedo() {
        return redo;
    }

    public char getRedoPromote() {
        return redoPromote;
    }

    public void setRedoPromote(char redoPromote) {
        this.redoPromote = redoPromote;
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
    /*
     * public ArrayList<Piece> getPieces() {
     * return pieces;
     * }
     */

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

    public boolean equals(Position compare) {

        boolean same = true;

        for(int r = 0; r < 8; r++) {

            for(int f = 0; f < 8; f++) {

                if(!pieces[r][f].equals(compare.getPieces()[r][f])) {

                    same = false;
                    break;

                }

            }

        }

        return same;

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
        this.systemTimeStart = -1;
        this.timerEnd = -1;

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

        // this.pieces = new ArrayList<Piece>();
        this.pieces = new Piece[8][8];
        this.white = isWhite;

        this.systemTimeStart = -1;
        this.timerEnd = -1;

        Piece[][] prevPieces = prev.getPieces();

        for (int r = 0; r < prevPieces.length; r++) {

            for (int c = 0; c < prevPieces[r].length; c++) {
                Piece old = prevPieces[r][c];
                if (old == null)
                    continue;

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

                if (piece != null) {
                    pieces[old.getSquare().getRank() - 1][old.getSquare().getFile() - 1] = piece;
                }
            }
        }

        if (move.isCapture()) {

            Square capSquare = move.getCaptureSquare();
            pieces[capSquare.getRank() - 1][capSquare.getFile() - 1] = null;

        }

        Square movePieceSq = move.getOrigin();
        Piece movePiece = pieces[movePieceSq.getRank() - 1][movePieceSq.getFile() - 1];

        if (movePiece.getCode() == 'K') {

            if (movePiece.isWhite())
                whiteKing = move.getDestination();
            else
                blackKing = move.getDestination();

        }

        Square destSq = move.getDestination();
        movePiece.setSquare(destSq);
        movePiece.setHasMoved(true);
        pieces[destSq.getRank() - 1][destSq.getFile() - 1] = movePiece;
        pieces[movePieceSq.getRank() - 1][movePieceSq.getFile() - 1] = null;

        if (move.isCastle()) {

            Piece rook = getPieceAtSquare(move.getRookOrigin());
            rook.setSquare(new Square(move.getDestination().getFile() == 7 ? 6 : 4, rook.getSquare().getRank()));

        }

        move.setText(prev);
        this.move = move;

        initMoves(checkForMate, game);

    }

    public void setPromoType(char promo, Game game) {

        if (promo == '0')
            return;

        Piece movePiece = move.getPiece();
        move.setPromoteType(promo);
        Square mps = move.getDestination();

        switch (promo) {
            case 'Q':
                setSquare(mps, new Queen(mps.getFile(), mps.getRank(), movePiece.isWhite()));
                break;
            case 'R':
                setSquare(mps, new Rook(mps.getFile(), mps.getRank(), movePiece.isWhite()));
                break;
            case 'B':
                setSquare(mps, new Bishop(mps.getFile(), mps.getRank(), movePiece.isWhite()));
                break;
            case 'N':
                setSquare(mps, new Knight(mps.getFile(), mps.getRank(), movePiece.isWhite()));
                break;
            case '?':
                setSquare(mps, movePiece);
                break;
        }

        if (!move.getPiece().equals(getPieceAtSquare(mps))) {

            initMoves(true, game);
            move.setText(game.getPositions().get(game.getCurrentPos() - 1));
        }

    }

    /**
     * Initializes the list of moves.
     * 
     * @param checkForMate
     * @param g
     */
    private void initMoves(boolean checkForMate, Game g) {

        this.moves = new ArrayList<Move>();

        ArrayList<Move> castleMoves = new ArrayList<Move>();

        for (int r = 0; r < pieces.length; r++) {

            for (int c = 0; c < pieces[r].length; c++) {
                Piece p = pieces[r][c];
                if (p == null)
                    continue;

                ArrayList<Move> pMoves = p.getMoves(this);

                if (p.getCode() != 'K' || p.hasMoved() == true) {

                    moves.addAll(pMoves);

                } else {

                    for (Move m : pMoves) {

                        if (m.isCastle())
                            castleMoves.add(m);
                        else
                            moves.add(m);

                    }

                }

            }

        }

        ArrayList<Piece> ownPieces = getPiecesByAttacking(white ? whiteKing : blackKing);
        if (ownPieces.size() >= 1)
            inCheck = true;

        ArrayList<Piece> oppPieces = getPiecesByAttacking(!white ? whiteKing : blackKing);
        if (oppPieces.size() >= 1)
            givingCheck = true;

        if (checkForMate) {
            setCheckMate(g);

            if (this.inCheck == false) {
                for (Move c : castleMoves) {

                    if ((c.getRookOrigin().getFile() == 1
                            && canPieceMoveToSquare(c.getPiece(), new Square(4, c.getOrigin().getRank())))
                            ||
                            (c.getRookOrigin().getFile() == 8
                                    && canPieceMoveToSquare(c.getPiece(), new Square(6, c.getOrigin().getRank())))) {

                        Position test = new Position(this, c, g, !c.isWhite(), false);
                        if (!test.isGivingCheck())
                            moves.add(c);

                    }

                }
            }
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

        for (int r = 0; r < pieces.length; r++) {

            for (int c = 0; c < pieces[r].length; c++) {

                Piece p = pieces[r][c];
                if (p == null)
                    continue;

                if (p.isWhite())
                    whitePoints += p.getPoints();
                else
                    blackPoints += p.getPoints();

            }

        }

        if (white && blackPoints > whitePoints || !white && whitePoints > blackPoints)
            return 0;

        if (white)
            return whitePoints - blackPoints;
        else
            return blackPoints - whitePoints;

    }

    /**
     * Sets whether or not the position is check mate. (The color whose turn it is
     * isn't able to make any moves and is in check.)
     * 
     * @param g The game to check.
     */
    private void setCheckMate(Game g) {

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

        pieces = new Piece[8][8];

        boolean color = true;
        for (int i = 0; i < 2; i++) {

            for (int j = 0; j < 8; j++) {

                setSquare(new Square(j + 1, color ? 2 : 7), new Pawn(j + 1, color ? 2 : 7, color));

            }

            // Kings
            Piece king = new King(5, color ? 1 : 8, color);
            if (color)
                whiteKing = king.getSquare();
            else
                blackKing = king.getSquare();

            setSquare(king.getSquare(), king);

            // Rooks
            setSquare(new Square(1, color ? 1 : 8), new Rook(1, color ? 1 : 8, color));
            setSquare(new Square(8, color ? 1 : 8), new Rook(8, color ? 1 : 8, color));

            // Queen
            setSquare(new Square(4, color ? 1 : 8), new Queen(4, color ? 1 : 8, color));

            // Bishops
            setSquare(new Square(3, color ? 1 : 8), new Bishop(3, color ? 1 : 8, color));
            setSquare(new Square(6, color ? 1 : 8), new Bishop(6, color ? 1 : 8, color));

            // Knight
            setSquare(new Square(2, color ? 1 : 8), new Knight(2, color ? 1 : 8, color));
            setSquare(new Square(7, color ? 1 : 8), new Knight(7, color ? 1 : 8, color));

            color = false;
        }

    }

    public void setSquare(Square s, Piece p) {

        pieces[s.getRank() - 1][s.getFile() - 1] = p;

    }

    /**
     * Finds the first occurance of a piece at the given square.
     * 
     * @param s The square to search for a piece at
     * @return The {@link Piece} object. Will be {@code null}
     *         if no
     *         piece is at the square.
     */
    /*
     * public Piece getPieceAtSquare(Square s) {
     * 
     * int index = findPiece(s);
     * 
     * Piece piece = null;
     * if (index >= 0)
     * piece = pieces.get(index);
     * 
     * return piece;
     * 
     * }
     */
    public Piece getPieceAtSquare(Square s) {
        return pieces[s.getRank() - 1][s.getFile() - 1];
    }

    /**
     * Gets a list of the pieces that are attacking the given square (able to
     * capture the piece occupying it.)
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
     * Gets a list of the pieces that are able to move to a given square.
     * 
     * @param s The square to search for moves to.
     * @return An {@link ArrayList} of {@link Piece} objects
     */
    public ArrayList<Piece> getPiecesByCanMoveTo(Square s) {

        ArrayList<Piece> pieces = new ArrayList<Piece>();

        for (int i = 0; i < moves.size(); i++) {

            Move m = moves.get(i);
            if (m.getDestination().equals(s))
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

    public boolean isInsufficientMaterial() {

        ArrayList<Piece> list = getPiecesAsArray();

        if (list.size() > 4)
            return false;

        //King and king
        if (list.size() == 2)
            return true;

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).getCode() == 'K') {
                list.remove(i);
                --i;
            }

        }

        //King against king and bishop / king against king and knight
        if (list.size() == 1 && (list.get(0).getCode() == 'B' || list.get(0).getCode() == 'N'))
            return true;
        else if (list.size() == 2) {

            Piece one = list.get(0);
            Piece two = list.get(1);

            //King and bishop against king and bishop, with both being on squares of same color
            if (one.isWhite() != two.isWhite()
                    && one.getSquare().isLightSquare() == two.getSquare().isLightSquare())
                return true;

        }

        return false;

    }

    public ArrayList<Piece> getPiecesAsArray() {

        ArrayList<Piece> list = new ArrayList<Piece>();

        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {

                Piece p = pieces[r][f];
                if (p != null) {
                    list.add(p);
                }

            }

        }

        return list;
    }

    /**
     * Checks if a piece can move to the given square.
     * 
     * @param piece  The piece to check
     * @param square The square to check for
     * @return Whether or not the location can be moved to
     */
    public boolean canPieceMoveToSquare(Piece piece, Square square) {

        ArrayList<Piece> attackers = getPiecesByCanMoveTo(square);

        for (int i = 0; i < attackers.size(); i++) {

            if (attackers.get(i).equals(piece))
                return true;

        }

        return false;

    }

    public ArrayList<Move> getPieceMoves(Piece piece) {

        ArrayList<Move> pieceMoves = new ArrayList<Move>();

        for (int i = 0; i < moves.size(); i++) {

            if (moves.get(i).getPiece().equals(piece))
                pieceMoves.add(moves.get(i));

        }

        return pieceMoves;

    }

}
