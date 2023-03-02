package game;

import java.util.ArrayList;

public class Move {

    private Square origin;
    private Square destination;

    private Piece piece;
    private Piece capturePiece;
    private Piece rook;

    private char pieceType;
    private char promoteType;

    private boolean capture;
    private boolean enPassant;
    private boolean white;
    private boolean castle;

    private String moveText;

    public boolean equals(Move compare) {

        return origin.equals(compare.getOrigin()) && destination.equals(compare.getDestination());

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

    public Square getDestination() {
        return destination;
    }

    public Square getOrigin() {
        return origin;
    }

    public char getPieceType() {
        return pieceType;
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

    public int getMoveDistance() {

        int fileDistance = (int) Math.abs(origin.getFile() - destination.getFile());
        int rankDistance = (int) Math.abs(origin.getRank() - destination.getRank());

        return (fileDistance > rankDistance) ? fileDistance : rankDistance;

    }

    private Piece findRook(Position pos) {

        if (!castle)
            return null;

        boolean kingSide = destination.getFile() == 7;

        return pos.getPieceAtSquare(new Square(kingSide ? 8 : 1, white ? 1 : 8));

    }

    public char getPromoteType() {
        return promoteType;
    }

    public void setPromoteType(char promoteType) {

        if (moveText == null) {
            moveText = "" + promoteType;
            this.promoteType = promoteType;
            return;
        }

        int i = moveText.lastIndexOf(this.promoteType);
        if (i > -1) {
            moveText = moveText.substring(0, i) + promoteType + moveText.substring(i + 1);
        } else {
            moveText += promoteType;
        }

        this.promoteType = promoteType;
    }

    public Move(Square origin, Square destination, Position pos) throws Exception {

        this.origin = origin;
        this.destination = destination;

        if (!origin.isValid())
            throw new Exception("Invalid origin.");
        if (!destination.isValid())
            throw new Exception("Invalid destination.");

        Piece originPiece = pos.getPieceAtSquare(origin);

        if (originPiece == null)
            throw new Exception("There is no piece at that square.");

        this.white = originPiece.isWhite();

        this.pieceType = originPiece.getCode();

        // long nt = System.nanoTime();
        this.enPassant = checkIfEnPassant(pos);
/*         long t = System.nanoTime();
        if (t - nt > 150)
            System.out.println("ep: " + (t - nt)); */

        // nt = System.nanoTime();
        this.capture = checkIfCapture(pos);
/*         t = System.nanoTime();
        if (t - nt > 150)
            System.out.println("cap: " + (t - nt)); */

        // nt = System.nanoTime();
        this.promoteType = checkIfPromote() ? '?' : '0';
 /*        t = System.nanoTime();
        if (t - nt > 150)
            System.out.println("promo: " + (t - nt)); */

        // nt = System.nanoTime();
        this.castle = checkIfCastle(pos);
/*         t = System.nanoTime();
        if (t - nt > 150)
            System.out.println("cap: " + (t - nt)); */

        this.piece = pos.getPieceAtSquare(origin);
        this.capturePiece = pos.getPieceAtSquare(getCaptureSquare());

    }

    public void setText(Position pos) {

        String str = "";

        if (castle) {

            str += destination.getFile() == 7 ? "0-0" : "0-0-0";
            moveText = str;
            return;

        }

        if (pieceType != 'P') {
            str += pieceType;
            str += getModifier(pos);
        } else {
            if (capture)
                str += (char) (origin.getFile() + 96);
        }

        if (capture)
            str += "x";

        str += destination;

        if (promoteType != '0' && pieceType == 'P')
            str += promoteType;

        moveText = str;

    }

    public String getMoveText() {

        return moveText;

    }

    private String getModifier(Position pos) {

        ArrayList<Piece> attackers = pos.getPiecesByAttacking(destination);

        int modFile = -1;
        int modRank = -1;

        for (int i = 0; i < attackers.size() && (modFile == -1 || modRank == -1); i++) {

            Piece a = attackers.get(i);

            if (a.getCode() != pieceType || a.getSquare().equals(origin))
                continue;

            if (a.getSquare().getFile() == origin.getFile()) {

                modRank = origin.getRank();

            }

            if (a.getSquare().getRank() == origin.getRank()) {

                modFile = origin.getFile();

            }

        }

        String modifier = "";

        if (modFile > -1)
            modifier += (char) (modFile + 96);

        if (modRank > -1)
            modifier += modRank;

        return modifier;

    }

    private boolean checkIfEnPassant(Position pos) {

        if (pieceType != 'P')
            return false;

        if (origin.getFile() == destination.getFile())
            return false;

        if (white && destination.getRank() != 6 || !white && destination.getRank() != 3)
            return false;

        Piece destP = pos.getPieceAtSquare(destination);

        if (destP != null)
            return false;

        Piece p = pos.getPieceAtSquare(new Square(destination.getFile(), destination.getRank() + (white ? -1 : 1)));
        if (p == null || p.getCode() != 'P')
            return false;

        Move prevMove = pos.getMove();

        if (prevMove.getMoveDistance() != 2)
            return false;

        return true;

    }

    private boolean checkIfCapture(Position pos) throws Exception {

        if (enPassant)
            return true;

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

            } else if (getCaptureSquare().getFile() != origin.getFile() && cap == null) {
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

        if (pieceType != 'P')
            return false;

        if (white && destination.getRank() != 8 || !white && destination.getRank() != 1)
            return false;

        return true;

    }

    public Square getRookLocation() {

        if (!castle)
            return null;

        boolean kingSide = destination.getFile() == 7;

        int file = kingSide ? 8 : 1;
        int rank = white ? 1 : 8;

        return new Square(file, rank);

    }

    private boolean checkIfCastle(Position pos) throws Exception {

        if (pieceType != 'K' || getMoveDistance() == 1)
            return false;

        if (((white && destination.getRank() != 1) || (!white && destination.getRank() != 8))
                || (destination.getFile() != 3 || destination.getFile() != 7))
            throw new Exception("Invalid castle location.");

        Piece king = pos.getPieceAtSquare(origin);

        if (king == null || king.hasMoved())
            throw new Exception("King has already moved, cannot castle.");

        boolean kingSide = getMoveDistance() == 2;
        int file = kingSide ? 8 : 1;
        int rank = white ? 1 : 8;

        Piece rook = pos.getPieceAtSquare(new Square(file, rank));

        if (rook == null || rook.hasMoved())
            throw new Exception("Rook already moved, cannot castle.");

        return true;

    }

    public Move(String move, Position pos, boolean white) throws Exception {

        move = move.replaceAll("O", "0");
        move = move.replaceAll("=", "");

        this.capture = move.indexOf("x") > -1;
        this.white = white;
        this.enPassant = false;
        this.promoteType = '0';
        this.rook = findRook(pos);

        char check = move.charAt(move.length() - 1);
        if (check == '+' || check == '#') {
            move = move.substring(0, move.length() - 1);
        }

        // castling
        this.castle = false;
        if (move.matches("0-0-0") || move.matches("0-0")) {

            this.castle = true;
            this.origin = new Square(5, white ? 1 : 8);
            this.pieceType = 'K';

            if (move.matches("0-0-0"))
                this.destination = new Square(3, white ? 1 : 8);
            else
                this.destination = new Square(7, white ? 1 : 8);

        } else {

            // standard move

            // piece type
            String pString = move.substring(0, 1);
            if (pString.matches("[a-h]"))
                pString = "P";

            char piece = pString.charAt(0);

            if (pieceType == 'K')
                piece = 'K';

            if (!(piece + "").matches("[KQRBNP]"))
                throw new Exception("Invalid piece type.");
            this.pieceType = piece;
            if (pieceType == 'P')
                move = "P" + move;

            // destination square
            String dest = "";
            int d = move.length();
            for (; d > 1; d--) {

                dest = move.substring(d - 2, d);
                if (dest.matches("[a-h][1-8]"))
                    break;

            }
            int dFile = dest.charAt(0) - 96;
            int dRank = Integer.parseInt(dest.substring(1, 2));

            if (dFile < 1 || dRank < 1 || dFile > 8 || dRank > 8)
                throw new Exception("Destination out of bounds.");

            destination = new Square(dFile, dRank);

            // origin square
            int prevFile = -1;
            int prevRank = -1;
            if (d - 2 != 1) {

                String first = move.substring(1, 2);
                if (first.matches("[a-h]"))
                    prevFile = first.charAt(0) - 96;
                else if (first.matches("[1-8]"))
                    prevRank = Integer.parseInt(first);

                String second = move.substring(2, 3);
                if (prevRank == -1 && second.matches("[1-8]")) {
                    prevRank = Integer.parseInt(second);
                }

            }

            ArrayList<Piece> attacking = new ArrayList<Piece>();

            for (int i = 0; i < pos.getMoves().size(); i++) {

                Move a = pos.getMoves().get(i);
                if (a.getDestination().equals(destination) && a.isWhite() == white)
                    attacking.add(a.getPiece());

            }

            for (int i = 0; i < attacking.size(); i++) {
                Piece a = attacking.get(i);
                if (a.getCode() == piece) {

                    if (prevFile > 0) {
                        if (a.getSquare().getFile() != prevFile)
                            continue;
                    }

                    if (prevRank > 0) {
                        if (a.getSquare().getRank() != prevRank)
                            continue;
                    }

                    origin = a.getSquare();

                }
            }

            if (origin == null)
                throw new Exception("No piece can reach the destination square.");

            // pawn moves - en passant & promotion
            if (piece == 'P') {

                // en passant
                if (pos.getPieceAtSquare(destination) == null && pos.getPieceAtSquare(
                        new Square(destination.getFile(), destination.getRank() + (white ? 1 : -1))) != null) {

                    enPassant = true;
                }

                // promotion
                String lastChar = move.substring(move.length() - 1);
                if (lastChar.matches("[KQRBN]")) {

                    this.promoteType = move.charAt(move.length() - 1);

                }

            }

        }

        if (!capture && pos.getPieceAtSquare(destination) != null)
            throw new Exception("Move not marked as a capture, but is a capture.");

        if (capture && pos.getPieceAtSquare(destination) == null)
            throw new Exception("Move marked as a capture, but is not a capture.");

    }
}