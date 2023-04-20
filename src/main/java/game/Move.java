package game;

import java.util.ArrayList;

import game.pieces.Piece;

/**
 * A representation of a chess move, that comes from a {@link Position}.
 */
public class Move {

    /**
     * The position this move is being made from.
     */
    private final Position position;

    /**
     * The starting square of the piece being moved.
     */
    private Square origin;

    /**
     * The ending square of the piece being moved.
     */
    private Square destination;

    /**
     * The piece being moved.
     */
    private Piece piece;

    /**
     * The piece being captured. Will be {@code null} if the move is not a capture
     * move.
     */
    private Piece capturePiece;

    /**
     * The rook being castled with. Will be {@code null} if the move is not a castle
     * move.
     */
    private Piece rook;

    /**
     * The type of piece to promote to.
     * 
     * <p>
     * <ul>
     * <li>0 - No promote type, not a promotion move.
     * <li>? - Promotion move, but promote type not yet supplied.
     * <li>Q - Queen
     * <li>R - Rook
     * <li>B - Bishop
     * <li>N - Knight
     */
    private char promoteType;

    /**
     * Whether or not this move is a capture move.
     */
    private boolean capture;

    /**
     * Whether or not this move is an en passant move.
     */
    private boolean enPassant;

    /**
     * Whether or not this is a castle move.
     */
    private boolean castle;

    /**
     * Whether or not white is making this move.
     */
    private boolean white;

    /**
     * The short algebraic notation (SAN) for this move.
     * 
     * <p>
     * Details can be found at
     * https://en.wikipedia.org/wiki/Algebraic_notation_(chess)
     */
    private String moveNotation;

    public Position getPosition() {
        return position;
    }

    public Square getOrigin() {
        return origin;
    }

    public Square getDestination() {
        return destination;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getCapturePiece() {
        return capturePiece;
    }

    public Piece getRook() {
        return rook;
    }

    public char getPromoteType() {
        return promoteType;
    }

    public boolean isCapture() {
        return capture;
    }

    public boolean isEnPassant() {
        return enPassant;
    }

    public boolean isCastle() {
        return castle;
    }

    public boolean isWhite() {
        return white;
    }

    public String getMoveNotation() {
        return moveNotation;
    }

    /**
     * @return the move described by its origin square and destination square. For
     *         algebraic notation, use {@link #getMoveNotation()} instead.
     * 
     * @see #getMoveNotation()
     */
    @Override
    public String toString() {
        return origin.toString() + destination.toString() + (promoteType == '0' ? "" : promoteType);
    }

    /**
     * Checks whether or not the given move is equal to this move. Will be based on
     * origin and destination, as well as if the move is a castle move.
     */
    @Override
    public boolean equals(Object compare) {

        if (!(compare instanceof Move))
            return false;

        Move casted = (Move) (compare);

        return origin.equals(casted.getOrigin()) && destination.equals(casted.getDestination())
                && castle == casted.isCastle();

    }

    /**
     * @return Gets the destance between the {@link #origin} and
     *         {@link #destination} squares of the move.
     */
    public int getMoveDistance() {

        int fileDistance = (int) Math.abs(origin.getFile() - destination.getFile());
        int rankDistance = (int) Math.abs(origin.getRank() - destination.getRank());

        return (fileDistance > rankDistance) ? fileDistance : rankDistance;

    }

    /**
     * Sets the {@link #promoteType} of this move. If setting this after move has
     * been made, {@link Position#setPromote(char)} should be used instead.
     * 
     * @param promoteType The promote type to set the move to.
     * 
     * @see #promoteType
     * @see Position#setPromote(char)
     */
    public void setPromoteType(char promoteType) {

        if (moveNotation == null) {

            moveNotation = "" + promoteType;
            this.promoteType = promoteType;

            return;

        }

        int i = moveNotation.lastIndexOf(this.promoteType);

        if (i > -1)
            moveNotation = moveNotation.substring(0, i) + promoteType + moveNotation.substring(i + 1);
        else
            moveNotation += promoteType;

        this.promoteType = promoteType;

    }

    /**
     * Initializes the move, checking if it is valid.
     *
     * @throws Exception If the move is invalid.
     */
    private void initMove() throws Exception {

        white = piece.isWhite();
        enPassant = checkIfEnPassant();
        checkValidCastle();
        capture = checkIfCapture();
        promoteType = checkIfPromote() ? '?' : '0';

        capturePiece = capture ? position.getPieceAtSquare(getCaptureSquare()) : null;

    }

    /**
     * Initializes a new, non-castling move.
     * 
     * @param origin      The origin square of the move.
     * @param destination The destination square of the move.
     * @param position    The position the move being made from.
     * @throws Exception If the move is invalid.
     */
    public Move(Square origin, Square destination, Position position) throws Exception {

        this(origin, destination, position, false);

    }

    /**
     * Initializes a new move.
     * 
     * @param origin      The origin square of the move.
     * @param destination The destination square of the move.
     * @param position    The position the move being made from.
     * @param castle      If the move is a castle move. Used to disambiguate when
     *                    the king could move to the square or castle to it.
     * @throws Exception If the move is invalid.
     */
    public Move(Square origin, Square destination, Position position, boolean castle) throws Exception {

        this.position = position;

        this.origin = origin;
        this.destination = destination;

        if (!origin.isValid())
            throw new Exception("Invalid origin.");

        if (!destination.isValid())
            throw new Exception("Invalid destination.");

        this.piece = position.getPieceAtSquare(origin);

        if (piece == null)
            throw new Exception("There is no piece at that square.");

        this.castle = castle;

        initMove();

    }

    /**
     * @return The square that the piece being captured is on before this move.
     */
    public Square getCaptureSquare() {

        if (enPassant) {
            return new Square(destination.getFile(), destination.getRank() + (white ? -1 : 1));
        } else
            return destination;

    }

    /**
     * @return The square the castling rook is on before this move is made.
     *         {@code null} if not a castle move.
     */
    public Square getRookOrigin() {

        return rook == null ? null : rook.getSquare();

    }

    /**
     * @return The square the castling rook is on after the move is made.
     *         {@code null} if not a castle move.
     */
    public Square getRookDestination() {

        if (!castle)
            return null;

        boolean kingSide = destination.getFile() == 7;

        int file = kingSide ? 6 : 4;
        int rank = white ? 1 : 8;

        return new Square(file, rank);

    }

    /**
     * Sets/updates the {@link #moveNotation}.
     */
    public void updateMoveNotation() {

        String str = "";

        if (castle) {

            str += destination.getFile() == 7 ? "0-0" : "0-0-0";
            moveNotation = str;

            return;

        }

        if (piece.getCode() != 'P') {

            str += piece.getCode();
            str += getModifier();

        } else {

            if (capture)
                str += (char) (origin.getFile() + 96);

        }

        if (capture)
            str += "x";

        str += destination;

        if (promoteType != '0' && piece.getCode() == 'P')
            str += "=" + promoteType;

        moveNotation = str;

    }

    /**
     * Gets the "modifier" that should be included in the move notation for this
     * move.
     * 
     * <p>
     * The modifier is the section of the move notation that disambiguates when
     * there are multiple of the same kind and color of piece that can move to the
     * given square.
     * 
     * @return The modifier of the move notation.
     */
    private String getModifier() {

        ArrayList<Piece> attackers = position.getPiecesByCanMoveTo(destination);

        int modFile = -1;
        int modRank = -1;

        boolean sameFile = false;
        boolean sameRank = false;
        boolean other = false;

        for (int i = 0; i < attackers.size() && (modFile == -1 || modRank == -1); i++) {

            Piece a = attackers.get(i);

            if (a.getCode() != piece.getCode() || a.getSquare().equals(origin))
                continue;

            if (a.getSquare().getFile() == origin.getFile()) {
                sameFile = true;
            }
            if (a.getSquare().getRank() == origin.getRank()) {
                sameRank = true;
            }

            other = true;

        }

        if (sameRank) {
            modFile = origin.getFile();
        }

        if (sameFile) {
            modRank = origin.getRank();
        }

        if (modRank == -1 && modFile == -1 && other) {
            modFile = origin.getFile();
        }

        String modifier = "";

        if (modFile > -1)
            modifier += (char) (modFile + 96);

        if (modRank > -1)
            modifier += modRank;

        return modifier;

    }

    /**
     * @return If the move is an en passant move.
     */
    private boolean checkIfEnPassant() {

        if (piece.getCode() != 'P')
            return false;

        if (origin.getFile() == destination.getFile())
            return false;

        if (white && destination.getRank() != 6 || !white && destination.getRank() != 3)
            return false;

        final Piece destinationPiece = position.getPieceAtSquare(destination);

        if (destinationPiece != null)
            return false;

        if (position.getEnPassantTarget() != null && position.getEnPassantTarget().equals(destination))
            return true;

        final Piece p = position
                .getPieceAtSquare(new Square(destination.getFile(), destination.getRank() + (white ? -1 : 1)));
        if (p == null || p.getCode() != 'P')
            return false;

        final Move prevMove = position.getMove();

        if (prevMove == null
                || (prevMove.getMoveDistance() != 2 || prevMove.getDestination().getFile() != destination.getFile()))
            return false;

        return true;

    }

    /**
     * @return If the move is a capture.
     * @throws Exception If the move is not a valid capture.
     */
    private boolean checkIfCapture() throws Exception {

        if (enPassant)
            return true;

        if (castle)
            return false;

        final Piece curr = position.getPieceAtSquare(origin);
        final Piece cap = position.getPieceAtSquare(getCaptureSquare());

        if (cap != null && cap.isWhite() == curr.isWhite())
            throw new Exception("Cannot capture your own piece.");

        if (curr.getCode() == 'P') {

            if (getCaptureSquare().getFile() == origin.getFile()) {

                if (cap != null)
                    throw new Exception("Cannot capture going forward.");
                else
                    return false;

            } else if (cap == null) {
                throw new Exception("Pawn cannot capture with no piece there.");
            }

        }

        return cap != null;

    }

    /**
     * Checks if the move is a valid castle move.
     */
    private void checkValidCastle() throws Exception {

        if (!castle)
            return;

        if (piece.getCode() != 'K'/* || getMoveDistance() == 1 */)
            throw new Exception("Castler is not a king.");

        if (((white && destination.getRank() != 1) || (!white && destination.getRank() != 8))
                || (destination.getFile() != 3 && destination.getFile() != 7))
            throw new Exception("Invalid castle location.");

        Piece king = position.getPieceAtSquare(origin);

        if (king == null || king.hasMoved())
            throw new Exception("King has already moved, cannot castle.");

        boolean aSide = destination.getFile() == 3;
        Piece rook = position.getRook(aSide, piece.isWhite());

        if (rook == null || rook.hasMoved())
            throw new Exception("Rook already moved, cannot castle.");

        this.rook = rook;

        if (position.isInCheck())
            throw new Exception("Cannot castle out of check.");

    }

    /**
     * @return If the move is a promotion move.
     */
    private boolean checkIfPromote() {

        if (piece.getCode() != 'P')
            return false;

        if (white && destination.getRank() != 8 || !white && destination.getRank() != 1)
            return false;

        return true;

    }

}