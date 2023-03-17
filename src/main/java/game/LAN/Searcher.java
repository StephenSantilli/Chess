package game.LAN;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Searcher {

    private static final int SEARCH_RETRIES = 5;

    private DatagramSocket socket;

    private Thread listenThread, emitThread;

    private ArrayList<Host> hosts;

    public ArrayList<Host> getHosts() {
        return hosts;
    }

    public Searcher() throws Exception {

        socket = new DatagramSocket(Client.PORT);

        hosts = new ArrayList<Host>();
        // socket.close();
    }

    public void search() {

        listenThread = new Thread(listener);
        emitThread = new Thread(emitter);

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

                byte[] buf = new byte[15];
                DatagramPacket packet = new DatagramPacket(buf, 15);
                socket.receive(packet);
                Host add = new Host(new String(buf), packet.getAddress());
                if (!hosts.contains(add) && !add.getName().trim().equals("c"))
                    hosts.add(add);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    };

    private Runnable emitter = () -> {
        try {
            String send = "c";
            DatagramPacket packet = new DatagramPacket(send.getBytes(), send.length(),
                    InetAddress.getByName("255.255.255.255"), Client.PORT);

            for (int i = 0; i < SEARCH_RETRIES; i++) {

                try {

                    socket.send(packet);
                    Thread.sleep(500);

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

        } catch (Exception e) {

        }

    };

}
