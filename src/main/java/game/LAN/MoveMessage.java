package game.LAN;

import game.Square;

public class MoveMessage extends Message {

    private final Square origin, destination;
    private final char promoteType;
    private final long timerEnd;

    public MoveMessage(Square origin, Square destination, char promoteType, long timerEnd) {
        
        super("move", origin.toString(), destination.toString(), promoteType + "", timerEnd + "");
    
        this.origin = origin;
        this.destination = destination;
        this.promoteType = promoteType;
        this.timerEnd = timerEnd;
        
    }

    public MoveMessage(String msg) throws Exception {

        super(msg);

        if (args.size() != 5)
            throw new Exception("Invalid move message.");

        origin = new Square(args.get(1));
        destination = new Square(args.get(2));

        promoteType = args.get(3).charAt(0);
        if (promoteType != '0' && promoteType != 'Q' && promoteType != 'R' && promoteType != 'B' && promoteType != 'N')
            throw new Exception("Invalid promote type.");

        timerEnd = Long.parseLong(args.get(4));

    }

    public Square getOrigin() {
        return origin;
    }

    public Square getDestination() {
        return destination;
    }

    public char getPromoteType() {
        return promoteType;
    }

    public long getTimerEnd() {
        return timerEnd;
    }

}
