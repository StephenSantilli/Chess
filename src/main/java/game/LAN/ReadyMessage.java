package game.LAN;

import game.Player;

public class ReadyMessage extends Message {

    private final int oppColor;
    private final String name;
    private final String fen;
    private final long timePerSide;
    private final long timePerMove;

    public ReadyMessage(int oppColor, String name, String fen, long timePerSide, long timePerMove) {

        super("ready", oppColor + "", name, fen, timePerSide + "", timePerMove + "");

        this.oppColor = oppColor;
        this.name = name;
        this.fen = fen;
        this.timePerSide = timePerSide;
        this.timePerMove = timePerMove;

    }

    public ReadyMessage(String msg) throws Exception {

        super(msg);

        if (args.size() != 6)
            throw new Exception("Invalid ready message.");

        try {
            this.oppColor = Integer.parseInt(args.get(1));
        } catch (Exception e) {
            throw new Exception("Invalid opponent color.");
        }

        if (oppColor != Challenge.CHALLENGE_WHITE && oppColor != Challenge.CHALLENGE_BLACK)
            throw new Exception("Invalid opponent color.");

        this.name = args.get(2);

        if (!name.matches(Player.NAME_REGEX))
            throw new Exception("Invalid name.");

        this.fen = args.get(3);

        try {
            this.timePerSide = Long.parseLong(args.get(4));
        } catch (Exception e) {
            throw new Exception("Invalid time per side.");
        }

        try {
            this.timePerMove = Long.parseLong(args.get(5));
        } catch (Exception e) {
            throw new Exception("Invalid time per move.");
        }

    }

    public String getFen() {
        return fen;
    }

    public int getOppColor() {
        return oppColor;
    }

    public String getName() {
        return name;
    }

    public long getTimePerSide() {
        return timePerSide;
    }

    public long getTimePerMove() {
        return timePerMove;
    }

}
