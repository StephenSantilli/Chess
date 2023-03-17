package game.LAN;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server {

    private static final int SEARCH_RETRIES = 5;

    private DatagramSocket socket;

    private Thread listenThread, emitThread;

    private ArrayList<Host> hosts;

    public ArrayList<Host> getHosts() {
        return hosts;
    }

    private String name;

    public Server(String name) throws Exception {

        socket = new DatagramSocket(Client.PORT);

        this.name = name;

        //socket.close();
    }

    public void start() {

        listenThread = new Thread(listener);
        //emitThread = new Thread(emitter);

        listenThread.start();
        emitThread.start();

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
                DatagramPacket packet = new DatagramPacket(buf, 5);
                socket.receive(packet);
                new Thread(new ServerSender(packet.getAddress(), name)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    };

}
