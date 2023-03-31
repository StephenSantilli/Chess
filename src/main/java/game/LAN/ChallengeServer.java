package game.LAN;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import game.GameSettings;

/**
 * The server to be started when a challenge is created and you are waiting for
 * others to search for it.
 */
public class ChallengeServer {

    private DatagramSocket udpSocket;
    private ServerSocket tcpSocket;

    private Thread listenThread;
    private Thread tcpThread;

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

        listenThread = new Thread(searchListener, "Server Listener");
        listenThread.start();

        tcpThread = new Thread(connectionListener, "Server Emitter");
        tcpThread.start();

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

            while (true) {

                Socket connection = tcpSocket.accept();

                client = new Client(connection, challenge.getName(), challenge.getColor(),
                        new GameSettings(challenge.getTimePerSide(), challenge.getTimePerMove(), false, false, true,
                                true),
                        gameCreatedCallback);

            }

        } catch (Exception e) {
        }

    };

    private Runnable searchListener = () -> {

        try {

            while (true) {

                byte[] buf = new byte[10];

                DatagramPacket packet = new DatagramPacket(buf, 10);
                udpSocket.receive(packet);

                new Thread(() -> {
                    try {

                        DatagramPacket pack = new DatagramPacket((challenge.toString()).getBytes(),
                                challenge.toString().length(),
                                packet.getAddress(), Client.PORT);

                        udpSocket.send(pack);

                    } catch (Exception er) {
                        er.printStackTrace();
                    }
                }, "Challenge Sender");

            }

        } catch (Exception e) {
        }

    };

}
