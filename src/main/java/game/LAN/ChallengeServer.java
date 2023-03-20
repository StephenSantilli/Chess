package game.LAN;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import game.GameSettings;

/**
 * The server to be started when a challenge is created and you are waiting for others to search for it.
 */
public class ChallengeServer {

    private DatagramSocket udpSocket;
    private ServerSocket tcpSocket;

    private Thread listenThread;

    private Challenge challenge;
    private Runnable gameCreatedCallback;

    private Client client;

    public Client getClient() {
        return client;
    }

    public ChallengeServer(Challenge challenge, Runnable gameCreatedCallback) throws Exception {

        this.challenge = challenge;
        this.gameCreatedCallback = gameCreatedCallback;
        
        udpSocket = new DatagramSocket(Client.PORT);
        udpSocket.setBroadcast(true);

        tcpSocket = new ServerSocket(Client.PORT);

    }

    public void start() {

        listenThread = new Thread(searchListener);
        listenThread.start();

    }

    public void stop() {

        try {
            udpSocket.close();
            tcpSocket.close();
        } catch (Exception e) {
        }

    }

    private Runnable connectionListener = () -> {

        try {

            tcpSocket.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), Client.PORT));
            while (true) {

                Socket connection = tcpSocket.accept();

                client = new Client(connection, challenge.getName(), challenge.getColor(), new GameSettings(challenge.getTimePerSide(), challenge.getTimePerMove(), false, false, true, true), gameCreatedCallback);

            }

        } catch (Exception e) {
        }

    };

    private Runnable searchListener = () -> {

        try {

            while (true) {

                byte[] buf = new byte[1];

                DatagramPacket packet = new DatagramPacket(buf, 1);
                udpSocket.receive(packet);

                new Thread(new ChallengeSender(new Challenge(challenge.getName(), challenge.getColor(),
                        challenge.getTimePerSide(), challenge.getTimePerMove(), packet.getAddress()), udpSocket)).start();

            }

        } catch (Exception e) {
        }

    };

}
