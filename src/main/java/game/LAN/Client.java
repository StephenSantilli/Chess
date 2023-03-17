package game.LAN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.sound.sampled.Port;

import game.GameListener;
import game.Game;
import game.Player;

public class Client implements GameListener {

    public static final int PORT = 49265;

    private ArrayList<Host> hosts;
    private String name;
    private ServerSocket server;

    private Socket connection;

    private Runnable onConnect;

    private LANPlayer player;

    public LANPlayer getPlayer() {
        return player;
    }

    Runnable startServer = () -> {

        try {

            server = new ServerSocket(PORT);
            System.out.println("listenin");
            while (true) {

                Socket s = server.accept();
                Thread t = new Thread(new SocketHandler(s, name, this));
                t.start();

            }

        } catch (Exception e) {

        }

    };

    private Runnable hostsChecker(String subnet, int start, int mult) {

        return new Runnable() {

            public void run() {

                int timeout = 500;

                for (int i = start; i < 255; i += mult) {
                    String host = subnet + (i);
                    // System.out.println("Checking " + host + "...");
                    try {

                        InetAddress h = InetAddress.getByName(host);
                        if (h.isReachable(timeout)) {

                            try {

                                Socket attempt = new Socket(h, PORT);
                                BufferedReader input = new BufferedReader(
                                        new InputStreamReader(attempt.getInputStream()));

                                String line = input.readLine();
                                if (line.startsWith("Chess;")) {
                                    hosts.add(new Host(line.substring(6), h));
                                    System.out.println("Host " + line.substring(6) + " on " + h.toString());
                                }
                                attempt.close();

                            } catch (Exception e) {

                            }

                        }

                    } catch (Exception e) {

                    }

                }

                --running;

            }

        };

    }

    void initGame(Socket sock) {

        connection = sock;
        Game game = new Game(-1, -1, false);
        player = new LANPlayer(name, sock, game);
        onConnect.run();
        player.start();

    }

    public void stop() {

        try {
            player.stop();
        } catch(Exception e) {

        }
        

    }

    public void startGame(Host opponent) throws Exception {

        Socket connection = new Socket();
        try {

            connection.bind(new InetSocketAddress(opponent.getAddress(), PORT));
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            Message msg = new Message(line);

            PrintWriter output = new PrintWriter(connection.getOutputStream(), true);
            output.println(new Message(name, ""));

        } catch (Exception e) {

            connection.close();
            throw new Exception("Connection unable to be established.");

        }

        connection.close();

    }

    public Client(String name, Runnable onConnect) throws Exception {

        this.name = name;
        this.onConnect = onConnect;

        hosts = new ArrayList<Host>();

        InetAddress siteLocal = getOwnAddress();

/*         if (siteLocal != null) {

            long time = System.currentTimeMillis();
            String hostA = siteLocal.getHostAddress();
            findHosts(hostA.substring(0, hostA.indexOf(".")));
            System.out.println("Took " + (System.currentTimeMillis() - time) / 1000 + " seconds");

        } */



        //new Thread(startServer).start();

    }

    private Runnable checkForServers = () -> {

        try {

            DatagramSocket d = new DatagramSocket(PORT, InetAddress.getByName("255.255.255.255"));
            
            d.send(new DatagramPacket(name.getBytes(), name.length(), d.getRemoteSocketAddress()));

            

            d.close();

        } catch(Exception e) {

        }
        

    };

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

    private int running = 0;

    public void findHosts(String subnet) throws IOException {

        int threadCount = 125;

        for (int i = 0; i < threadCount; i++) {

            Thread t = new Thread(hostsChecker(subnet, i, threadCount));

            t.start();
            ++running;

        }

        while (running > 0) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {

            }

        }

    }


    @Override
    public void moveMade() {

    }

    @Override
    public void undoMove() {

    }

    @Override
    public void redoMove() {

    }

    @Override
    public void resetMoves() {

    }

    @Override
    public void posChanged(int old, int curr) {

    }

    @Override
    public void gameOver() {

    }

    @Override
    public void timerChange() {

    }

    @Override
    public void pauseGame() {

    }

    @Override
    public void resumeGame() {

    }

}
