package game;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

public class LANClient {

    public static final int PORT = 49265;

    private ArrayList<InetAddress> hosts;

    public LANClient() throws Exception {

        //TODO: combine getting hosts with checking for socket availability for efficiency
        //TODO: setup server listening on port, have each client socket connection on a separate thread to allow for multiple
        //TODO: when checking for if a host is another chess program, connect to socket have server handle

        hosts = new ArrayList<InetAddress>();

        InetAddress siteLocal = null;
        Enumeration<NetworkInterface> is = NetworkInterface.getNetworkInterfaces();
        while (is.hasMoreElements()) {
            Enumeration<InetAddress> ads = is.nextElement().getInetAddresses();
            while (ads.hasMoreElements()) {
                InetAddress a = ads.nextElement();
                if (a.isSiteLocalAddress())
                    siteLocal = a;
            }
        }

        if (siteLocal != null) {
            long time = System.currentTimeMillis();
            String hostA = siteLocal.getHostAddress();
            getHosts(hostA.substring(0, hostA.length() - 2));
            System.out.println("Took " + (System.currentTimeMillis() - time) / 1000 + " seconds");
            for (InetAddress host : hosts) {
                try {
                    Socket s = new Socket();
                    s.connect(new InetSocketAddress(host, PORT), 100);
                    System.out.println("conn");
                    s.close();
                } catch (Exception e) {
                    System.out.println("cant on " + host.getHostAddress());
                }

            }

            // sock.accept();

            System.out.println("connected");
            // sock.close();
        }

    }

    public static void main(String[] args) throws Exception {
        new LANClient();
    }

    private int running = 0;

    private Runnable runner(String subnet, int start, int mult) {

        return new Runnable() {

            public void run() {
                // at least on windows, this definitely doesn't do anything below 1000ms--google
                // says may differ based on os
                int timeout = 250;

                for (int i = start; i < 255; i += mult) {
                    String host = subnet + (i);
                    // System.out.println("Checking " + host + "...");
                    try {
                        InetAddress h = InetAddress.getByName(host);
                        if (h.isReachable(timeout)) {
                            hosts.add(h);
                        }

                    } catch (Exception e) {

                    }

                }

                --running;

            }

        };

    }

    public void getHosts(String subnet) throws IOException {

        int threadCount = 100;

        for (int i = 0; i < threadCount; i++) {

            Thread t = new Thread(runner(subnet, i, threadCount));

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

}
