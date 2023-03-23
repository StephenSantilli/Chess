package game.LAN;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

import game.Game;

/**
 * Sends out a request for {@link Challenge}s on the local network.
 */
public class ChallengeSearcher {

    private static final int SEARCH_RETRIES = 3;
    private static final int SEARCH_MILLIS_BETWEEN = 1000;

    private DatagramSocket socket;

    private Thread listenThread, emitThread;

    private ArrayList<Challenge> challenges;

    private InetAddress ownAddress;

    private Runnable searchDoneCallback;

    public ArrayList<Challenge> getChallenges() {
        return challenges;
    }

    private Runnable emitter = () -> {

        try {

            String send = Game.VERSION;
            DatagramPacket packet = new DatagramPacket(send.getBytes(), send.length(),
                    InetAddress.getByName("255.255.255.255"), Client.PORT);

            for (int i = 0; i < SEARCH_RETRIES; i++) {

                try {

                    socket.send(packet);
                    Thread.sleep(SEARCH_MILLIS_BETWEEN);

                } catch (Exception e) {

                }

            }

        } catch (Exception e) {

        }

        if (searchDoneCallback != null)
            searchDoneCallback.run();

    };

    private Runnable listener = () -> {

        try {

            while (true) {

                byte[] buf = new byte[100];
                DatagramPacket packet = new DatagramPacket(buf, 100);
                socket.receive(packet);

                try {
                    Challenge add = new Challenge(packet);
                    if (add.getVersion().equals(Game.VERSION) && !packet.getAddress().equals(ownAddress) && !challenges.contains(add))
                        challenges.add(add);
                } catch (Exception e) {
                    continue;
                }

            }

        } catch (Exception e) {
        }

    };

    public ChallengeSearcher() throws Exception {

        ownAddress = getOwnAddress();

        challenges = new ArrayList<Challenge>();

        searchDoneCallback = null;

    }

    public void search(Runnable searchDoneCallback) throws Exception {

        this.searchDoneCallback = searchDoneCallback;

        socket = new DatagramSocket(Client.PORT);
        socket.setBroadcast(true);

        challenges.clear();

        listenThread = new Thread(listener, "Searcher Listener");
        listenThread.start();

        emitThread = new Thread(emitter, "Searcher Emitter");
        emitThread.start();

    }

    public void stop() {
        try {
            socket.close();
        } catch (Exception e) {

        }

    }

    private InetAddress getOwnAddress() throws IOException {

        Enumeration<NetworkInterface> is = NetworkInterface.getNetworkInterfaces();

        while (is.hasMoreElements()) {
            NetworkInterface ifsInterface = is.nextElement();
            Enumeration<InetAddress> ads = ifsInterface.getInetAddresses();
            while (ads.hasMoreElements()) {
                InetAddress a = ads.nextElement();
                if (a.isSiteLocalAddress())
                    return a;
            }
        }

        return null;

    }

}
