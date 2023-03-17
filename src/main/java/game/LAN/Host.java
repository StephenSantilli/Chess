package game.LAN;

import java.net.InetAddress;

public class Host {

    private String name;
    private InetAddress address;

    @Override
    public boolean equals(Object compare) {

        if (!(compare instanceof Host))
            return false;

        if (compare == this)
            return true;

        Host cast = (Host)compare;

        return name.equals(cast.getName()) && address.equals(cast.getAddress());

    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Host(String name, InetAddress address) {
        this.name = name;
        this.address = address;
    }

}
