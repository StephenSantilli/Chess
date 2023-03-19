package game.LAN;

import game.Position;
import game.Square;

public class MoveMessage implements Message {

    private Position position;

    public MoveMessage(Position position) throws Exception {

        if (position.getMove() == null)
            throw new Exception("There is no move associated with that position");

        this.position = position;

    }

    @Override
    public String getText() {

        String str = "";

        str += position.getMove().getOrigin().toString() + ";";
        str += position.getMove().getDestination().toString() + ";";
        str += position.getTimerEnd() + ";";

        return str;

    }

}
