package game.LAN;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server {

    private DatagramSocket socket;

    private Thread listenThread;

    private Challenge challenge;

    public Server(Challenge challenge) throws Exception {

        socket = new DatagramSocket(Client.PORT);

        this.challenge = challenge;

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
                byte[] buf = new byte[500];
                System.out.println("listening");
                DatagramPacket packet = new DatagramPacket(buf, 500);
                socket.receive(packet);
                System.out.println(packet.getAddress());
                new Thread(new ChallengeSender(new Challenge(challenge.getName(), challenge.getColor(), challenge.getTimePerSide(), challenge.getTimePerMove(), packet.getAddress()), socket)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    };

}
