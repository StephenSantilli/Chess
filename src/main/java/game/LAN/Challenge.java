package game.LAN;

import java.net.InetAddress;

public class Challenge {

    public static final int CHALLENGE_RANDOM = 0;
    public static final int CHALLENGE_WHITE = 1;
    public static final int CHALLENGE_BLACK = 2;

    private String name;

    private int color;

    private InetAddress address;



    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Challenge(String name, int color, InetAddress address) throws Exception {
        
        if(name.length() > 50) {

            throw new Exception("Name is too long.");

        }

        this.name = name;
        this.address = address;
        this.color = color;

    }

    public Challenge(byte[] input) {



    }

    @Override
    public boolean equals(Object compare) {

        if (!(compare instanceof Challenge))
            return false;

        if (compare == this)
            return true;

        Challenge cast = (Challenge) compare;

        return name.equals(cast.getName()) && address.equals(cast.getAddress());

    }

}
