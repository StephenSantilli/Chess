package game.LAN;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerSender implements Runnable {

    private String name;
    private InetAddress address;

    public ServerSender(InetAddress address, String name) {

        this.name = name;
        this.address = address;

    }

    public void run() {
        try {

            DatagramSocket sock = new DatagramSocket(Client.PORT);
            DatagramPacket packet = new DatagramPacket(name.getBytes(), 15, address, Client.PORT);

            sock.send(packet);

            sock.close();

        } catch(Exception e) {
            
        }


    }

}
