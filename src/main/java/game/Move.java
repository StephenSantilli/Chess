package game;

import java.util.ArrayList;

import game.pieces.Piece;

public class Move {

    private Square origin;
    private Square destination;

    private Piece piece;
    private Piece capturePiece;
    private Piece rook;

    private char promoteType;

    private boolean capture;
    private boolean enPassant;
    private boolean white;
    private boolean castle;

    private String moveNotation;

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

    public boolean isWhite() {
        return white;
    }

    public boolean isCastle() {
        return castle;
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
        return origin.toString() + " to " + destination.toString();
    }

    @Override
    public boolean equals(Object compare) {

        if (!(compare instanceof Move))
            return false;

        Move casted = (Move) (compare);

        return origin.equals(casted.getOrigin()) && destination.equals(casted.getDestination());

    }

    public int getMoveDistance() {

        int fileDistance = (int) Math.abs(origin.getFile() - destination.getFile());
        int rankDistance = (int) Math.abs(origin.getRank() - destination.getRank());

        return (fileDistance > rankDistance) ? fileDistance : rankDistance;

    }

    void setPromoteType(char promoteType) {

        if (moveNotation == null) {
            moveNotation = "" + promoteType;
            this.promoteType = promoteType;
            return;
        }

        int i = moveNotation.lastIndexOf(this.promoteType);
        if (i > -1) {
            moveNotation = moveNotation.substring(0, i) + promoteType + moveNotation.substring(i + 1);
        } else {
            moveNotation += promoteType;
        }

        this.promoteType = promoteType;

    }

    private void initMove(Position pos) throws Exception {

        this.white = piece.isWhite();
        this.enPassant = checkIfEnPassant(pos);
        checkValidCastle(pos);
        this.capture = checkIfCapture(pos);
        this.promoteType = checkIfPromote() ? '?' : '0';

        this.capturePiece = capture ? pos.getPieceAtSquare(getCaptureSquare()) : null;

    }

    public Move(Square origin, Square destination, Position pos) throws Exception {

        this(origin, destination, pos, false);

    }

    public Move(Square origin, Square destination, Position pos, boolean castle) throws Exception {

        this.origin = origin;
        this.destination = destination;

        if (!origin.isValid())
            throw new Exception("Invalid origin.");

        if (!destination.isValid())
            throw new Exception("Invalid destination.");

        this.piece = pos.getPieceAtSquare(origin);

        if (piece == null)
            throw new Exception("There is no piece at that square.");

        this.castle = castle;

        initMove(pos);

    }

    public void updateMoveNotation(Position pos) {

        String str = "";

        if (castle) {

            str += destination.getFile() == 7 ? "0-0" : "0-0-0";
            moveNotation = str;
            return;

        }

        if (piece.getCode() != 'P') {
            str += piece.getCode();
            str += getModifier(pos);
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

    private String getModifier(Position pos) {

        ArrayList<Piece> attackers = pos.getPiecesByCanMoveTo(destination);

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

    private boolean checkIfEnPassant(Position pos) {

        if (piece.getCode() != 'P')
            return false;

        if (origin.getFile() == destination.getFile())
            return false;

        if (white && destination.getRank() != 6 || !white && destination.getRank() != 3)
            return false;

        Piece destP = pos.getPieceAtSquare(destination);

        if (destP != null)
            return false;

        if (pos.getEnPassantDestination() != null && pos.getEnPassantDestination().equals(destination))
            return true;

        Piece p = pos.getPieceAtSquare(new Square(destination.getFile(), destination.getRank() + (white ? -1 : 1)));
        if (p == null || p.getCode() != 'P')
            return false;

        Move prevMove = pos.getMove();

        if (prevMove == null
                || (prevMove.getMoveDistance() != 2 || prevMove.getDestination().getFile() != destination.getFile()))
            return false;

        return true;

    }

    private boolean checkIfCapture(Position pos) throws Exception {

        if (enPassant)
            return true;

        if (castle)
            return false;

        Piece curr = pos.getPieceAtSquare(origin);
        Piece cap = pos.getPieceAtSquare(getCaptureSquare());

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

    public Square getCaptureSquare() {

        if (enPassant) {
            return new Square(destination.getFile(), destination.getRank() + (white ? -1 : 1));
        } else
            return destination;

    }

    private boolean checkIfPromote() {

        if (piece.getCode() != 'P')
            return false;

        if (white && destination.getRank() != 8 || !white && destination.getRank() != 1)
            return false;

        return true;

    }

    public Square getRookOrigin() {

        // if (!castle)
        // return null;

        // boolean kingSide = destination.getFile() == 7;

        // int file = kingSide ? 8 : 1;
        // int rank = white ? 1 : 8;

        // return new Square(file, rank);

        return rook.getSquare();

    }

    public Square getRookDestination() {

        if (!castle)
            return null;

        boolean kingSide = destination.getFile() == 7;

        int file = kingSide ? 6 : 4;
        int rank = white ? 1 : 8;

        return new Square(file, rank);

    }

    private void checkValidCastle(Position pos) throws Exception {

        if (!castle)
            return;

        if (piece.getCode() != 'K'/* || getMoveDistance() == 1 */)
            throw new Exception("Castler is not a king.");

        if (((white && destination.getRank() != 1) || (!white && destination.getRank() != 8))
                || (destination.getFile() != 3 && destination.getFile() != 7))
            throw new Exception("Invalid castle location.");

        Piece king = pos.getPieceAtSquare(origin);

        if (king == null || king.hasMoved())
            throw new Exception("King has already moved, cannot castle.");

        boolean aSide = destination.getFile() == 3;
        Piece rook = pos.getRook(aSide, piece.isWhite());

        if (rook == null || rook.hasMoved())
            throw new Exception("Rook already moved, cannot castle.");

        this.rook = rook;

        if (pos.isInCheck())
            throw new Exception("Cannot castle out of check.");

    }

}