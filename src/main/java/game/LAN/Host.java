package game.LAN;

import java.net.InetAddress;

public class Host {
    
    private String name;
    private InetAddress address;

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
