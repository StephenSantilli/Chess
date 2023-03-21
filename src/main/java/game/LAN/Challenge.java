package game.LAN;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import game.Game;
import game.Player;

public class Challenge {

    public static final int CHALLENGE_RANDOM = 0;
    public static final int CHALLENGE_WHITE = 1;
    public static final int CHALLENGE_BLACK = 2;

    private String version, name;
    private int color;
    private long timePerSide, timePerMove;
    private InetAddress address;

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public long getTimePerSide() {
        return timePerSide;
    }

    public long getTimePerMove() {
        return timePerMove;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Challenge(String name, int color, long timePerSide, long timePerMove, InetAddress address) throws Exception {

        this.version = Game.VERSION;
        this.name = name;
        this.color = color;
        this.timePerSide = timePerSide;
        this.timePerMove = timePerMove;
        this.address = address;

        checkIfValid();

    }

    public Challenge(DatagramPacket packet) throws Exception {

        String str = new String(packet.getData()).trim();

        Message msg = new Message(str);

        ArrayList<String> a = msg.getArgs();

        if (a.size() != 5) {
            throw new Exception("Invalid challenge.");
        }

        version = a.get(0);

        name = a.get(1);

        try {
            color = Integer.parseInt(a.get(2));
        } catch (Exception e) {
            throw new Exception("Invalid color.");
        }

        try {
            timePerSide = Integer.parseInt(a.get(3));
        } catch (Exception e) {
            throw new Exception("Invalid time per side.");
        }

        try {
            timePerMove = Integer.parseInt(a.get(4));
        } catch (Exception e) {
            throw new Exception("Invalid time per move.");
        }

        address = packet.getAddress();

        checkIfValid();

    }

    private void checkIfValid() throws Exception {

        if (name.length() > Player.MAX_NAME_LENGTH)
            throw new Exception("Your name is too long.");

        if (!name.matches(Player.NAME_REGEX))
            throw new Exception("Invalid name.");

        if (color < Challenge.CHALLENGE_RANDOM || color > CHALLENGE_BLACK)
            throw new Exception("Invalid color.");

    }

    public String toString() {

        String str = version + ";" + name + ";" + color + ";" + timePerSide + ";" + timePerMove + ";";
        return str;

    }

    @Override
    public boolean equals(Object compare) {

        if (address == null && compare == null)
            return true;
        else if (address == null)
            return false;

        if (!(compare instanceof Challenge))
            return false;

        if (compare == this)
            return true;

        Challenge cast = (Challenge) compare;

        return name.equals(cast.getName()) && address.equals(cast.getAddress());

    }

}
