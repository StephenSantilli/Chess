package game.LAN;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class Challenge {

    public static final int CHALLENGE_RANDOM = 0;
    public static final int CHALLENGE_WHITE = 1;
    public static final int CHALLENGE_BLACK = 2;

    public static final int MAX_NAME_LENGTH = 15;

    private String name;

    private int color, timePerSide, timePerMove;

    private InetAddress address;

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public int getTimePerSide() {
        return timePerSide;
    }

    public int getTimePerMove() {
        return timePerMove;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Challenge(String name, int color, int timePerSide, int timePerMove, InetAddress address) throws Exception {

        if (name.length() > MAX_NAME_LENGTH) {

            throw new Exception("Name is too long.");

        }

        this.name = name;
        this.address = address;
        this.color = color;
        this.timePerSide = timePerSide;
        this.timePerMove = timePerMove;

    }

    public Challenge(DatagramPacket packet) throws Exception {

        String str = new String(packet.getData()).trim();

        String[] a = str.split(";");

        if(a.length < 4) {
            throw new Exception("Invalid challenge.");
        }
        
        name = a[0];
        
        if(name.length() > MAX_NAME_LENGTH) throw new Exception("Invalid name length.");

        try {
            color = Integer.parseInt(a[1]);
        } catch(Exception e) {
            throw new Exception("Invalid color.");
        }

        try {
            timePerSide = Integer.parseInt(a[2]);
        } catch(Exception e) {
            throw new Exception("Invalid timePerSide.");
        }

        try {
            timePerMove = Integer.parseInt(a[3]);
        } catch(Exception e) {
            throw new Exception("Invalid timePerMove.");
        }

        address = packet.getAddress();

    }

    public String toString() {

        String str = name + ";" + color + ";" + timePerSide + ";" + timePerMove + ";";
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
