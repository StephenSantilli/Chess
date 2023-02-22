package game;

import java.util.ArrayList;

public class Game {

    private ArrayList<Position> positions;

    private int currentPos;

    private ArrayList<BoardListener> listeners;

    public void fireBoardUpdate() {

        for (BoardListener b : listeners) {

            b.boardUpdated();

        }

    }

    public char firePromptForPromote(Move move) {

        for (BoardListener b : listeners) {

            char c = b.promptForPromote(move);

            if (c == 'Q' || c == 'N' || c == 'B' || c == 'R')
                return c;

        }

        return '?';

    }

    public void addListener(BoardListener listener) {
        listeners.add(listener);
    }

    public Position getActivePos() {
        return positions.get(currentPos);
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public Game() {

        listeners = new ArrayList<BoardListener>();
        positions = new ArrayList<Position>();
        positions.add(new Position(this));
        currentPos = 0;

    }

    public boolean isWhiteTurn(boolean overall) {

        return positions.get(overall ? positions.size() - 1 : currentPos).isWhite();

    }

    public void makeMove(Move m) {

        if (currentPos != positions.size() - 1)
            return;

        Position prev = positions.get(positions.size() - 1);

        if (prev.isCheckMate() || m.isWhite() != isWhiteTurn(true))
            return;

        Move valid = null;
        for (int i = 0; i < prev.getMoves().size(); i++) {
            Move a = prev.getMoves().get(i);

            if (a.equals(m))
                valid = a;
        }

        if (valid == null)
            return;

        Position movePosition = new Position(positions.get(currentPos), valid, this, !isWhiteTurn(true), true);

        if (movePosition.isGivingCheck())
            return;

        if (movePosition.isGivingCheck() && movePosition.isInCheck())
            return;

        if (movePosition.isGivingCheck() && !prev.isInCheck())
            return;

        if (movePosition.getMove().isCapture() && movePosition.getMove().getCapturePiece().getCode() == 'K')
            return;

        positions.add(movePosition);
        currentPos = positions.size() - 1;

        fireBoardUpdate();

    }

    public void setCurrentPos(int currentPos) {

        if (currentPos >= positions.size() || currentPos < 0)
            return;

        this.currentPos = currentPos;

        fireBoardUpdate();

    }

    public boolean canUndo() {

        return currentPos == positions.size() - 1 && currentPos != 0;

    }

    public boolean canRedo() {

        return currentPos == positions.size() - 1 && positions.get(currentPos).getRedo() != null;

    }

    public void undoMove() {

        if (!canUndo())
            return;

        Position redo = positions.get(currentPos);

        positions.remove(currentPos);

        positions.get(currentPos - 1).setRedo(redo);

        if (redo.getMove().getPromoteType() != '0')
            redo.getMove().setPromoteType('?');

        currentPos = positions.size() - 1;

        fireBoardUpdate();

    }

    public void redoMove() {

        Position redo = positions.get(positions.size() - 1).getRedo();

        if (!canRedo())
            return;

        positions.add(redo);
        currentPos = positions.size() - 1;

        fireBoardUpdate();

    }

    public void importPosition(String moves) throws Exception {

        positions = new ArrayList<Position>();
        positions.add(new Position(this));
        currentPos = 0;

        int i = 1;

        int start = moves.indexOf(i + ". ");
        while (start > -1) {
            moves = moves.substring(start);
            moves = moves.replaceAll("[^A-z0-9.-]", " ");
            start = moves.indexOf((i + 1) + ". ");

            String[] strings;

            if (start > -1)
                strings = moves.substring(0, start).split(" ");
            else
                strings = moves.substring(0).split(" ");

            try {
                makeMove(new Move(strings[1], getActivePos(), true));
                makeMove(new Move(strings[2], getActivePos(), false));
            } catch (Exception e) {
                System.out.println(e);
                break;
            }

            ++i;

        }

        if (currentPos == 0)
            throw new Exception("Position import failed.");

        fireBoardUpdate();

    }

}
