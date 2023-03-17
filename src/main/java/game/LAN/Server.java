package game.LAN;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server {

    private static final int SEARCH_RETRIES = 5;

    private DatagramSocket socket;

    private Thread listenThread, emitThread;

    private ArrayList<Challenge> hosts;

    private String name;
    private int color;

    public ArrayList<Challenge> getHosts() {
        return hosts;
    }

    public Server(String name, int color) throws Exception {

        socket = new DatagramSocket(Client.PORT);

        this.name = name;
        this.color = color;

    }

    public void start() {

        listenThread = new Thread(listener);
        listenThread.start();

    }

    public void stop() {

        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Runnable listener = () -> {

        try {

            while (true) {
                byte[] buf = new byte[5];
                System.out.println("listening");
                DatagramPacket packet = new DatagramPacket(buf, 1);
                socket.receive(packet);
                System.out.println(packet.getAddress());
                new Thread(new ChallengeSender(new Challenge(name, color, packet.getAddress()), socket)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    };

}
