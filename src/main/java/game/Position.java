package game;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import game.pieces.Bishop;
import game.pieces.King;
import game.pieces.Knight;
import game.pieces.Pawn;
import game.pieces.Piece;
import game.pieces.Queen;
import game.pieces.Rook;

/**
 * A board position of a {@link Game}. Contains the pieces and their positions,
 * as well as the moves that can be made from this position.
 */
public class Position {

    /**
     * The number of moves made in this game (including the move that led to
     * this position.) Starts at {@code 0} for the default position as no moves have
     * been made.
     */
    private int moveNumber;

    /**
     * A 2D array that matches the board and stores the pieces.
     */
    private Piece[][] pieces;

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
     * that led to this position. Will be the opposite of {@link move#isWhite()}.
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
     * The timer end of the previous position, saved for if this position is
     * restored by calling {@link Game#redo()}.
     */
    private long redoTimerEnd;

    /**
     * The amount of time on the timer at the end of this position.
     */
    private long timerEnd;

    /**
     * Counter that counts the number of moves made since the last time a pawn was
     * moved or a capture was made. Once 50 moves have been completed (100 turns),
     * the game will be declared a draw. This number includes the move that led to
     * the current position. It will count up to 100.
     */
    private int fiftyMoveCounter;

    /**
     * The square that can be en passanted from this position.
     */
    private Square enPassantTarget;

    /** Whether or not mate has been checked. */
    private boolean mateChecked;

    /**
     * @return {@link #enPassantTarget}
     */
    public Square getEnPassantTarget() {
        return enPassantTarget;
    }

    /**
     * @return {@link #moveNumber}
     */
    public int getMoveNumber() {
        return moveNumber;
    }

    /**
     * @return {@link #pieces}
     */
    public Piece[][] getPieces() {
        return pieces;
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

    /**
     * Sets the redo {@link Position}.
     * 
     * @param redo The redo {@link Position}
     */
    public void setRedo(Position redo) {
        this.redo = redo;
    }

    public char getRedoPromote() {
        return redoPromote;
    }

    public void setRedoPromote(char redoPromote) {
        this.redoPromote = redoPromote;
    }

    public long getRedoTimerEnd() {
        return redoTimerEnd;
    }

    public void setRedoTimerEnd(long redoTimerEnd) {
        this.redoTimerEnd = redoTimerEnd;
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
    public boolean isCheckmate() {
        return checkMate;
    }

    public int getFiftyMoveCounter() {
        return fiftyMoveCounter;
    }

    /**
     * Returns a string representation of the move that led to this position.
     * 
     * @return A {@link String} containing the move text.
     */
    public String getMoveString() {

        String str = move.getMoveNotation();
        if (isCheckmate())
            str += "#";
        else if (isInCheck())
            str += "+";

        return str;

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
     * Gets the rook on the side specified and of the given color. Will only return
     * the rook that is on the home file of that color.
     * 
     * @param aRook Whether or not to search for the a-side rook or the h-side rook.
     * @param white Whether or not the rook is white.
     * @return The rook.
     */
    public Piece getRook(boolean aRook, boolean white) {

        final Square square = (white ? whiteKing : blackKing);

        Piece rook = null;

        if (!aRook) {

            for (int i = square.getFile() + 1; i <= 8 && rook == null; i++) {

                Piece pc = getPieceAtSquare(new Square(i, white ? 1 : 8));

                if (pc != null && pc.getCode() == 'R')
                    rook = pc;

            }

        } else {

            for (int i = square.getFile() - 1; i > 0 && rook == null; i--) {

                Piece pc = getPieceAtSquare(new Square(i, white ? 1 : 8));

                if (pc != null && pc.getCode() == 'R')
                    rook = pc;

            }

        }

        return rook;

    }

    /**
     * Creates a new {@link Position} object in the default starting position.
     */
    public Position() {

        white = true;
        this.mateChecked = false;

        this.moveNumber = 0;
        this.timerEnd = -1;
        this.fiftyMoveCounter = 0;

        initDefaultPosition();
        initMoves(true);

    }

    /**
     * Creates a new {@link Position} object from the previous position with the new
     * {@link Move}.
     * 
     * @param prev         The previous position to use as a baseline for this
     *                     position.
     * @param move         The move to be made.
     * @param white        Whether or not it is white's turn after this move is
     *                     made.
     * @param checkForMate Whether or not checkmate should be checked for.
     * @param promoteType  The type of piece to promote to.
     */
    public Position(Position prev, Move move, boolean white, boolean checkForMate, char promoteType)
            throws Exception {

        this.pieces = new Piece[8][8];
        this.mateChecked = false;

        this.timerEnd = -1;

        this.white = white;

        Piece[][] prevPieces = prev.getPieces();

        this.moveNumber = prev.getMoveNumber() + 1;

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

            pieces[capSquare.getRank() - 1][capSquare.getFile() - 1] = null;

        }

        final Piece movePiece = pieces[move.getOrigin().getRank() - 1][move.getOrigin().getFile() - 1];

        if (movePiece.getCode() == 'K') {

            if (movePiece.isWhite())
                whiteKing = move.getDestination();
            else
                blackKing = move.getDestination();

        }

        if (!movePiece.hasMoved() && movePiece.getCode() == 'P' && move.getMoveDistance() == 2) {
            enPassantTarget = new Square(move.getDestination().getFile(),
                    move.getDestination().getRank() + (move.isWhite() ? -1 : 1));
        }

        movePiece.setSquare(move.getDestination());
        movePiece.setHasMoved(true);

        pieces[move.getOrigin().getRank() - 1][move.getOrigin().getFile() - 1] = null;

        if (move.isCastle()) {

            final Piece rook = getPieceAtSquare(move.getRookOrigin());
            // rook.setSquare(new Square(move.getDestination().getFile() == 7 ? 6 : 4,
            // rook.getSquare().getRank()));
            rook.setSquare(move.getRookDestination());

            pieces[move.getRookOrigin().getRank() - 1][move.getRookOrigin().getFile() - 1] = null;
            pieces[move.getRookDestination().getRank() - 1][move.getRookDestination().getFile() - 1] = rook;

            rook.setHasMoved(true);

        }

        pieces[move.getDestination().getRank() - 1][move.getDestination().getFile() - 1] = movePiece;

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

        } else if (move.getPromoteType() != '0' && checkForMate) {

            if (move.getPromoteType() != 'Q' && move.getPromoteType() != 'R'
                    && move.getPromoteType() != 'B' && move.getPromoteType() != 'N')
                throw new Exception("Invalid promote type.");

            final Square mps = move.getDestination();

            switch (move.getPromoteType()) {
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

        move.updateMoveNotation();
        this.move = move;

        initMoves(checkForMate);

        if (move.isCapture() || move.getPiece().getCode() == 'P') {
            this.fiftyMoveCounter = 0;
        } else {
            this.fiftyMoveCounter = prev.getFiftyMoveCounter() + 1;
        }

    }

    /**
     * Creates a new {@link Position} object from the given starting position.
     * 
     * @param fen The Forsyth-Edwards Notation (FEN) of the position to start from.
     */
    public Position(String fen) throws Exception {

        String[] a = fen.split(" ");

        if (a.length != 6)
            throw new Exception("Invalid FEN.");

        this.pieces = new Piece[8][8];
        this.mateChecked = false;

        this.timerEnd = -1;

        String[] ranks = a[0].split("/");

        if (ranks.length != 8)
            throw new Exception("Invalid ranks.");

        if (a[1].equals("w"))
            white = true;
        else if (a[1].equals("b"))
            white = false;
        else
            throw new Exception("Invalid color to move.");

        try {
            fiftyMoveCounter = Integer.parseInt(a[4]);
        } catch (Exception e) {
            throw new Exception("Invalid 50 move counter.");
        }

        try {
            moveNumber = ((Integer.parseInt(a[5]) - 1) * 2) + (white ? 0 : 1);
        } catch (Exception e) {
            throw new Exception("Invalid move number.");
        }

        if (fiftyMoveCounter < 0)
            throw new Exception("Fifty move counter cannot be less than 0.");

        if (moveNumber + 1 < 1)
            throw new Exception("Move number cannot be less than 1.");

        for (int r = 0; r < 8; r++) {

            for (int i = 0, f = 0; i < ranks[r].length(); i++) {

                char c = ranks[r].charAt(i);

                if (Character.isDigit(c)) {
                    int amt = Integer.parseInt(c + "");
                    f += amt;
                    continue;
                } else {

                    if (!(c + "").matches("[KQBRNPkqbrnp]"))
                        throw new Exception("Unexpected piece type.");
                    boolean white = Character.isUpperCase(c);
                    switch (Character.toUpperCase(c)) {
                        case 'K':
                            pieces[7 - r][f] = new King(f + 1, 8 - r, white);

                            if (white)
                                whiteKing = new Square(f + 1, 8 - r);
                            else
                                blackKing = new Square(f + 1, 8 - r);

                            break;
                        case 'Q':
                            pieces[7 - r][f] = new Queen(f + 1, 8 - r, white);
                            break;
                        case 'R':
                            pieces[7 - r][f] = new Rook(f + 1, 8 - r, white);
                            break;
                        case 'B':
                            pieces[7 - r][f] = new Bishop(f + 1, 8 - r, white);
                            break;
                        case 'N':
                            pieces[7 - r][f] = new Knight(f + 1, 8 - r, white);
                            break;
                        case 'P':
                            pieces[7 - r][f] = new Pawn(f + 1, 8 - r, white);

                            if ((white && r != 6) || (!white && r != 1))
                                pieces[7 - r][f].setHasMoved(true);

                            break;
                        default:
                            throw new Exception("Unexpected piece.");
                    }

                    ++f;

                }

            }

        }

        if (!a[3].equals("-")) {
            try {
                enPassantTarget = new Square(a[3]);
            } catch (Exception e) {
                throw new Exception("Invalid en passant target square.");
            }
        }

        Piece wkr = getPieceAtSquare(new Square(8, 1));
        Piece wqr = getPieceAtSquare(new Square(1, 1));
        Piece bkr = getPieceAtSquare(new Square(8, 8));
        Piece bqr = getPieceAtSquare(new Square(1, 8));

        if (wkr != null && wkr.getCode() == 'R')
            wkr.setHasMoved(true);

        if (wqr != null && wqr.getCode() == 'R')
            wqr.setHasMoved(true);

        if (bkr != null && bkr.getCode() == 'R')
            bkr.setHasMoved(true);

        if (bqr != null && bqr.getCode() == 'R')
            bqr.setHasMoved(true);

        if (!a[2].equals("-")) {
            for (int i = 0; i < a[2].length(); i++) {

                char c = a[2].charAt(i);
                switch (c) {
                    case 'K':
                        if (wkr != null && wkr.getCode() == 'R')
                            wkr.setHasMoved(false);
                        break;
                    case 'Q':
                        if (wqr != null && wqr.getCode() == 'R')
                            wqr.setHasMoved(false);
                        break;
                    case 'k':
                        if (bkr != null && bkr.getCode() == 'R')
                            bkr.setHasMoved(false);
                        break;
                    case 'q':
                        if (bqr != null && bqr.getCode() == 'R')
                            bqr.setHasMoved(false);
                        break;
                    default:
                        throw new Exception("Invalid castle status.");
                }

            }
        }

        initMoves(true);

    }

    /**
     * Constructs an array of all of the pieces that are not on the board in this
     * position.
     * 
     * @param white The color of the captured pieces.
     * @return The array of captured pieces.
     */
    public ArrayList<Piece> getCapturedPieces(boolean white) {

        ArrayList<Piece> cap = new ArrayList<>();

        int k = 1, q = 1, r = 2, b = 2, n = 2, p = 8;

        for (int rk = 0; rk < 8; rk++) {

            for (int f = 0; f < 8; f++) {

                Piece a = pieces[rk][f];
                if (a != null && a.isWhite() == white) {

                    switch (a.getCode()) {
                        case 'K':
                            --k;
                            break;
                        case 'Q':
                            --q;
                            break;
                        case 'R':
                            --r;
                            break;
                        case 'B':
                            --b;
                            break;
                        case 'N':
                            --n;
                            break;
                        case 'P':
                            --p;
                            break;
                    }
                }

            }

        }

        while (k > 0) {
            cap.add(new King(0, 0, white));
            --k;
        }

        while (q > 0) {
            cap.add(new Queen(0, 0, white));
            --q;
        }

        while (r > 0) {
            cap.add(new Rook(0, 0, white));
            --r;
        }

        while (b > 0) {
            cap.add(new Bishop(0, 0, white));
            --b;
        }

        while (n > 0) {
            cap.add(new Knight(0, 0, white));
            --n;
        }

        while (p > 0) {
            cap.add(new Pawn(0, 0, white));
            --p;
        }

        return cap;

    }

    public int calculatePieceDelta() {

        int delta = 0;
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                Piece p = pieces[r][f];
                if (p != null) {
                    delta += (p.isWhite() ? 1 : -1) * p.getPoints();
                }
            }
        }

        return delta;

    }

    /**
     * Finds a move based on the given origin and destination.
     * 
     * <p>
     * Castle moves can be represented as either the king capturing the respective
     * rook, or as the king moving to the castle destination square. However, in
     * variants like Chess960, the king may be able to move to the same square it
     * can castle to. In that case, the normal move always overrides the castle
     * move.
     * Castle moves can always be unambiguously represented as the king moving to
     * the rook's square.
     * 
     * @param origin      The origin square of the move to search for
     * @param destination The destination square of the move to search for
     * @return The move found, or {@code null} if none found.
     */
    public Move findMove(Square origin, Square destination) {

        if (origin == null || destination == null || !origin.isValid() || !destination.isValid())
            return null;

        // Finding the move based on the origin and destination.
        // Castle moves should be king moving to rook's square.
        Move move = null, maybe = null;
        for (int i = 0; move == null && i < moves.size(); i++) {

            Move a = moves.get(i);

            if (!a.isCastle() && a.getOrigin().equals(origin) && a.getDestination().equals(destination))
                move = a;
            else if (a.isCastle() && getPieceAtSquare(destination) != null
                    && getPieceAtSquare(destination).equals(a.getRook())) {
                move = a;
            } else if (a.isCastle() && a.getOrigin().equals(origin) && a.getDestination().equals(destination)) {
                // will mark a castle move that matches the origin and destination, but doesn't
                // set it as the move in case there is a non castle move with the same origin
                // and destination
                maybe = a;
            }

        }

        if (move == null && maybe != null)
            move = maybe;

        return move;

    }

    /**
     * Sets the promote type and updates the moves accordingly.
     * 
     * @param promo The type of piece to promote to.
     * @throws Exception If the promotion is invalid.
     */
    public void setPromote(char promo) throws Exception {

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

            initMoves(true);
            move.updateMoveNotation();

        }

    }

    /**
     * Initializes the list of moves.
     * 
     * @param checkForMate If checkmate should be checked for.
     */
    private void initMoves(boolean checkForMate) {

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

        for (int x = 0; x < castleMoves.size(); x++) {

            Move c = castleMoves.get(x);

            boolean thruCheck = false;
            final Piece king = c.getPiece();
            final boolean aSide = king.getSquare().getFile() > c.getDestination().getFile();

            for (int i = king.getSquare().getFile() + (aSide ? -1 : 1); thruCheck == false
                    && ((aSide && i >= c.getDestination().getFile()
                            || !aSide && i <= c.getDestination().getFile())); i += (aSide ? -1 : 1)) {

                ArrayList<Piece> attackers = getPiecesByCanMoveTo(new Square(i, c.isWhite() ? 1 : 8));
                attackers.removeIf((pc) -> pc.isWhite() == c.isWhite());

                if (attackers.size() > 0)
                    thruCheck = true;

            }

            if (thruCheck || c.isWhite() != white) {
                castleMoves.remove(x);
                --x;
            }

        }

        if (checkForMate) {

            setCheckmate();

            if (this.inCheck == false) {

                // castleMoves.removeIf(m -> m.isWhite() != white);

                moves.addAll(castleMoves);

            }
        }
    }

    /**
     * Sets whether or not the position is check mate. (The color whose turn it is
     * isn't able to make any moves and is in check.) Also will filter out any moves
     * that are not legal.
     */
    private void setCheckmate() {

        this.mateChecked = true;
        this.checkMate = true;

        for (int i = 0; i < moves.size(); i++) {

            Move m = moves.get(i);

            if (m.isWhite() != isWhite()) {
                moves.remove(i);
                --i;
                continue;
            }

            try {
                Position test = new Position(this, m, !white, false, '0');
                if (!test.isGivingCheck())
                    checkMate = false;
                else {
                    moves.remove(i);
                    --i;
                }
            } catch (Exception e) {
                e.printStackTrace();
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
     * Gets the point total of the pieces that {@code white} has.
     * 
     * @param white The color
     * @return A point total of the pieces.
     */
    public int getPoints(boolean white) {

        int points = 0;

        for (int r = 0; r < 8; r++) {

            for (int f = 0; f < 8; f++) {

                Piece a = pieces[r][f];

                if (a.isWhite() == white)
                    points += a.getPoints();

            }

        }

        return points;

    }

    /**
     * Sets the square of a piece.
     * 
     * @param square The square to set the piece to.
     * @param piece  The piece to set.
     */
    public void setSquare(Square square, Piece piece) {

        pieces[square.getRank() - 1][square.getFile() - 1] = piece;

    }

    /**
     * Finds the first occurance of a piece at the given square.
     * 
     * @param square The square to search for a piece at
     * @return The {@link Piece} object. Will be {@code null}
     *         if no
     *         piece is at the square.
     */
    public Piece getPieceAtSquare(Square square) {
        return pieces[square.getRank() - 1][square.getFile() - 1];
    }

    /**
     * Gets a list of the pieces that are attacking the given square (able to
     * capture the piece occupying it.)
     * 
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
     * @param square The square to search for moves to.
     * @return An {@link ArrayList} of {@link Piece} objects
     */
    public ArrayList<Piece> getPiecesByCanMoveTo(Square square) {

        ArrayList<Piece> pieces = new ArrayList<Piece>();

        for (int i = 0; i < moves.size(); i++) {

            Move m = moves.get(i);
            if (m.getDestination().equals(square))
                pieces.add(m.getPiece());

        }

        return pieces;

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

    /**
     * Gets the moves the given piece can make.
     * 
     * @param piece The piece to get the moves of.
     * @return A list of the moves the piece can make.
     */
    public ArrayList<Move> getPieceMoves(Piece piece) {

        ArrayList<Move> pieceMoves = new ArrayList<Move>();

        for (int i = 0; i < moves.size(); i++) {

            if (moves.get(i).getPiece().equals(piece))
                pieceMoves.add(moves.get(i));

        }

        return pieceMoves;

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
     * @return If the position has insufficient pieces on both sides to reach
     *         checkmate.
     */
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

    /**
     * @return If the position is stalemate.
     */
    public boolean isStalemate() {

        return moves.size() == 0;

    }

    /**
     * @return The pieces as an {@link ArrayList}.
     */
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
     * Outputs the Forsyth–Edwards Notation (FEN) for this position.
     */
    @Override
    public String toString() {

        String fen = "";

        // Pieces
        for (int r = 7; r >= 0; r--) {

            int noPieceCount = 0;

            for (int f = 0; f < 8; f++) {

                Piece p = pieces[r][f];

                if (p == null)
                    ++noPieceCount;
                else {

                    if (noPieceCount > 0) {
                        fen += noPieceCount;
                        noPieceCount = 0;
                    }

                    fen += p.isWhite() ? (p.getCode() + "") : (p.getCode() + "").toLowerCase();

                }

            }

            if (noPieceCount > 0) {
                fen += noPieceCount;
                noPieceCount = 0;
            }

            if (r > 0)
                fen += "/";

        }

        // Active Color
        fen += " " + (isWhite() ? "w" : "b");

        // Castle availability

        fen += " " + (canCastle(true, true) ? "K" : "") + (canCastle(true, false) ? "Q" : "")
                + (canCastle(false, true) ? "k" : "") + (canCastle(false, false) ? "q" : "");

        if (fen.charAt(fen.length() - 1) == ' ')
            fen += '-';

        fen += " " + (enPassantTarget == null ? "-" : enPassantTarget.toString());

        // Fifty-move rule clock
        fen += " " + fiftyMoveCounter;

        // Fullmove number
        fen += " " + ((int) Math.ceil(moveNumber / 2.0) + 1);

        return fen;

    }

    /**
     * Checks the castling availability in the current position. Based on whether or
     * not the king and rook can move, and whether or not there is a king or rook on
     * the given side. Does not mean that castling can occur during the current
     * turn, as temporary blocks like check may still be present.
     * 
     * @param white    The color to check if can castle.
     * @param kingSide Whether to check castling king side (h side) or queen side (a
     *                 side).
     * @return Whether or the given castle is possible.
     */
    public boolean canCastle(boolean white, boolean kingSide) {

        final Piece king = getPieceAtSquare(white ? whiteKing : blackKing);

        if (king.hasMoved())
            return false;

        final Piece rook = getPieceAtSquare(new Square(white ? 1 : 8, kingSide ? 8 : 1));

        if (rook != null && !rook.hasMoved()) {
            return true;
        }

        return false;

    }

    /**
     * Finds an available move by its short algebraic notation (SAN).
     * 
     * @param move The SAN of the move to find.
     * @return The move found from the SAN.
     * @throws Exception If the move notation is invalid or {@link #mateChecked} is
     *                   not {@code true}.
     */
    public Move getMoveBySAN(String move) throws Exception {

        if (!mateChecked)
            throw new Exception(
                    "Cannot find move by SAN if position has not been initialized with checkForMate as true.");

        move = move.trim();

        Square o = null;
        Square d = null;
        char piece = '0';
        int oFile = -1;
        int oRank = -1;

        if (move.startsWith("0-0-0") || move.startsWith("O-O-O")) {

            o = new Square((white ? whiteKing : blackKing).getFile(), (white ? whiteKing : blackKing).getRank());
            d = new Square(3, white ? 1 : 8);
            piece = 'K';

        } else if (move.startsWith("0-0") || move.startsWith("O-O")) {

            o = new Square((white ? whiteKing : blackKing).getFile(), (white ? whiteKing : blackKing).getRank());
            d = new Square(7, white ? 1 : 8);
            piece = 'K';

        } else {

            Pattern pat = Pattern.compile("[a-h][1-8]");
            Matcher m = pat.matcher(move);

            int lastSquare = -1;
            while (m.find()) {
                lastSquare = m.start();
            }

            if (lastSquare <= -1)
                throw new Exception("No destination square.");

            try {
                d = new Square(move.substring(lastSquare, lastSquare + 2));
            } catch (Exception e) {
                throw new Exception("Invalid destination square.");
            }

            String first = move.substring(0, 1);
            int start = 0;

            if (first.matches("[KQRBNP]")) {
                piece = first.charAt(0);
                start = 1;
            } else if (first.matches("[a-h]"))
                piece = 'P';
            else
                throw new Exception("Invalid piece type.");

            if (move.length() > 3) {
                String modifier = move.substring(start, lastSquare);

                if (modifier.matches("[a-h][1-8]x?")) {
                    o = new Square(modifier);
                } else if (modifier.matches("[a-h]x?")) {
                    oFile = (int) (modifier.charAt(0)) - 96;
                } else if (modifier.matches("[1-8]x?")) {
                    oRank = (int) (modifier.charAt(0)) - 48;
                }
            }

        }

        ArrayList<Move> possibleMoves = new ArrayList<Move>();

        for (Move mo : moves) {

            if (mo.getPiece().getCode() != piece || mo.isWhite() != isWhite())
                continue;

            if (mo.getDestination().equals(d)) {

                if (o != null && mo.getOrigin().equals(o))
                    possibleMoves.add(mo);
                else if (oFile > -1 && oRank > -1) {
                    if (mo.getOrigin().getFile() == oFile && mo.getOrigin().getRank() == oRank)
                        possibleMoves.add(mo);
                } else if (oFile > -1) {
                    if (mo.getOrigin().getFile() == oFile)
                        possibleMoves.add(mo);
                } else if (oRank > -1) {
                    if (mo.getOrigin().getRank() == oRank)
                        possibleMoves.add(mo);
                } else {
                    possibleMoves.add(mo);
                }

            }

        }

        if (possibleMoves.size() == 0)
            throw new Exception("Move not found.");

        if (possibleMoves.size() > 1)
            throw new Exception("Multiple possible moves.");

        Move found = possibleMoves.get(0);

        Matcher findPromo = Pattern.compile("=(?<promo>[QRBN])").matcher(move);
        if (findPromo.find()) {

            char promo = findPromo.group("promo").charAt(0);

            if (promo != 'Q' && promo != 'R' && promo != 'B' && promo != 'N')
                throw new Exception("Invalid promote type supplied.");

            found.setPromoteType(promo);

        }

        return found;

    }

}
