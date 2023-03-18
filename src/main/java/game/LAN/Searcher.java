package game.LAN;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

public class Searcher {

    private static final int SEARCH_RETRIES = 5;

    private DatagramSocket socket;

    private Thread listenThread, emitThread;

    private ArrayList<Challenge> hosts;

    private InetAddress ownAddress;

    public ArrayList<Challenge> getHosts() {
        return hosts;
    }

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

    private Runnable listener = () -> {

        try {

            while (true) {

                byte[] buf = new byte[100];
                DatagramPacket packet = new DatagramPacket(buf, 100);
                socket.receive(packet);

                try {
                    Challenge add = new Challenge(packet);
                if (packet.getAddress().equals(ownAddress) && !hosts.contains(add))
                    hosts.add(add);
                } catch(Exception e) {
                    e.printStackTrace();
                    continue;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    };

    public Searcher() throws Exception {

        socket = new DatagramSocket(Client.PORT);

        hosts = new ArrayList<Challenge>();

        ownAddress = getOwnAddress();

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

    private InetAddress getOwnAddress() throws IOException {

        Enumeration<NetworkInterface> is = NetworkInterface.getNetworkInterfaces();

        while (is.hasMoreElements()) {
            Enumeration<InetAddress> ads = is.nextElement().getInetAddresses();
            while (ads.hasMoreElements()) {
                InetAddress a = ads.nextElement();
                if (a.isSiteLocalAddress())
                    return a;
            }
        }

        return null;

    }

}
