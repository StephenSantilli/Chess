package game;

import java.util.ArrayList;

import game.pieces.Bishop;
import game.pieces.King;
import game.pieces.Knight;
import game.pieces.Pawn;
import game.pieces.Piece;
import game.pieces.Queen;
import game.pieces.Rook;

public class Position {

    public static final int NO_OFFER = 0;
    public static final int WHITE = 1;
    public static final int BLACK = 2;

    private Piece[][] pieces;

    private ArrayList<Piece> capturedPieces;

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

    /**
     * The color of who offered the draw. If multiple draw offers, will only include
     * last.
     * 
     * <ul>
     * <li>0 - No draw offer
     * <li>1 - White offered
     * <li>2 - Black offered
     */
    private int drawOfferer;

    /**
     * Counter that counts the number of moves made since the last time a pawn was
     * moved or a capture was made. Once 50 moves have been completed (100 turns),
     * the game will be declared a draw. This number includes the move that led to
     * the current position.
     */
    private int fiftyMoveCounter;

    /** All of the pieces in the position. Does not include captured pieces. */
    public Piece[][] getPieces() {
        return pieces;
    }

    public ArrayList<Piece> getCapturedPieces() {
        return capturedPieces;
    }

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

    public int getDrawOfferer() {
        return drawOfferer;
    }

    public void setDrawOfferer(int drawOfferer) {
        this.drawOfferer = drawOfferer;
    }

    public int getFiftyMoveCounter() {
        return fiftyMoveCounter;
    }

    /**
     * Checks if the board positions are exactly equal. Will return {@code false}
     * even if pieces
     * are in the same spots but their {@link Piece#hasMoved()} property is
     * different. It will also return {@code false} if the pieces are in the same
     * positions, but it is the opposite color's turn.
     */
    @Override
    public boolean equals(Object compare) {

        if (!(compare instanceof Position))
            return false;

        Position casted = (Position) (compare);

        boolean same = true;

        if (isWhite() != casted.isWhite())
            same = false;

        for (int r = 0; same && r < 8; r++) {

            for (int f = 0; same && f < 8; f++) {

                if (!pieces[r][f].equals(casted.getPieces()[r][f])) {

                    same = false;

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
        this.capturedPieces = new ArrayList<Piece>();
        this.drawOfferer = NO_OFFER;
        this.fiftyMoveCounter = 0;

    }

    /**
     * Creates a new {@link Position} object from the previous position with the new
     * {@link Move}.
     * 
     * @param prev         The previous position to use as a baseline for this
     *                     position.
     * @param move         The move to be made.
     * @param game         The game this position is associated with.
     * @param white        Whether or not it is white's turn after this move is
     *                     made.
     * @param checkForMate Whether or not checkmate should be checked for.
     */
    public Position(Position prev, Move move, Game game, boolean white, boolean checkForMate, char promoteType)
            throws Exception {

        this.pieces = new Piece[8][8];
        this.white = white;

        this.systemTimeStart = -1;
        this.timerEnd = -1;

        this.drawOfferer = NO_OFFER;

        Piece[][] prevPieces = prev.getPieces();

        this.capturedPieces = new ArrayList<Piece>();
        capturedPieces.addAll(prev.getCapturedPieces());

        for (int r = 0; r < prevPieces.length; r++) {

            for (int f = 0; f < prevPieces[r].length; f++) {

                final Piece old = prevPieces[r][f];

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

                if (piece != null)
                    pieces[old.getSquare().getRank() - 1][old.getSquare().getFile() - 1] = piece;

            }
        }

        if (move.isCapture()) {

            final Square capSquare = move.getCaptureSquare();

            capturedPieces.add(pieces[capSquare.getRank() - 1][capSquare.getFile() - 1]);
            pieces[capSquare.getRank() - 1][capSquare.getFile() - 1] = null;

        }

        final Piece movePiece = pieces[move.getOrigin().getRank() - 1][move.getOrigin().getFile() - 1];

        if (movePiece.getCode() == 'K') {

            if (movePiece.isWhite())
                whiteKing = move.getDestination();
            else
                blackKing = move.getDestination();

        }

        movePiece.setSquare(move.getDestination());
        movePiece.setHasMoved(true);

        pieces[move.getDestination().getRank() - 1][move.getDestination().getFile() - 1] = movePiece;
        pieces[move.getOrigin().getRank() - 1][move.getOrigin().getFile() - 1] = null;

        if (move.isCastle()) {

            final Piece rook = getPieceAtSquare(move.getRookOrigin());
            rook.setSquare(new Square(move.getDestination().getFile() == 7 ? 6 : 4, rook.getSquare().getRank()));
            rook.setHasMoved(true);

        }

        if (move.getPromoteType() == '?' && checkForMate) {

            if (promoteType != 'Q' && promoteType != 'R' && promoteType != 'B' && promoteType != 'N')
                throw new Exception("Invalid promote type.");

            move.setPromoteType(promoteType);
            final Square mps = move.getDestination();

            switch (promoteType) {
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
            }

        }

        move.setText(prev);
        this.move = move;

        initMoves(checkForMate, game);

        if(move.isCapture() || move.getPiece().getCode() == 'P') {
            this.fiftyMoveCounter = 0;
        } else {
            this.fiftyMoveCounter = prev.getFiftyMoveCounter() + 1;
        }

    }

    void setPromote(char promo, Game game) throws Exception {

        if (promo != '?' && promo != 'Q' && promo != 'R' && promo != 'B' && promo != 'N')
            throw new Exception("Invalid promote type.");

        move.setPromoteType(promo);

        final Piece movePiece = move.getPiece();
        final Square mps = move.getDestination();

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
            move.setText(game.getLastPos());

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

                final Piece p = pieces[r][c];
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
                            || (c.getRookOrigin().getFile() == 8
                                    && canPieceMoveToSquare(c.getPiece(), new Square(6, c.getOrigin().getRank())))) {

                        try {

                            final Position test = new Position(this, c, g, !c.isWhite(), false, '0');

                            if (!test.isGivingCheck())
                                moves.add(c);

                        } catch (Exception e) {

                        }

                    }

                }
            }

        }
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

            try {
                Position test = new Position(this, m, g, !white, false, '0');
                if (!test.isGivingCheck())
                    checkMate = false;
                else {
                    moves.remove(i);
                    --i;
                }
            } catch (Exception e) {

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

    /**
     * Gets the point total of the pieces that {@code white} has captured.
     * 
     * @param white The color of the capturer, or the opposite of the pieces that
     *              were captured.
     * @return A point total of the pieces captured.
     */
    public int getCapturedPiecePoints(boolean white) {

        int points = 0;

        for (Piece cap : capturedPieces) {

            if (cap.isWhite() == white)
                continue;

            points += cap.getPoints();

        }

        return points;

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

    public boolean isInsufficientMaterial() {

        ArrayList<Piece> list = getPiecesAsArrayList();

        if (list.size() > 4)
            return false;

        // King and king
        if (list.size() == 2)
            return true;

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).getCode() == 'K') {
                list.remove(i);
                --i;
            }

        }

        // King against king and bishop / king against king and knight
        if (list.size() == 1 && (list.get(0).getCode() == 'B' || list.get(0).getCode() == 'N'))
            return true;
        else if (list.size() == 2) {

            Piece one = list.get(0);
            Piece two = list.get(1);

            // King and bishop against king and bishop, with both being on squares of same
            // color
            if (one.isWhite() != two.isWhite()
                    && one.getSquare().isLightSquare() == two.getSquare().isLightSquare())
                return true;

        }

        return false;

    }

    public ArrayList<Piece> getPiecesAsArrayList() {

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
