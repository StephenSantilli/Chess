package game;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Iterator;

public class LANClient {

    public static final int PORT = 49269; 

    public static void main(String[] args) throws IOException {



        System.out.println();
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

            // String hostA = siteLocal.getHostAddress();
            // LANClient.checkHosts(hostA.substring(0, hostA.length() - 2));

            Socket sock = new Socket(siteLocal, PORT);
            sock.bind(sock.getLocalSocketAddress());
            while(!sock.isConnected()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }

            System.out.println("connected");

        }

    }

    private static int running = 0;

    private static Runnable runner(String subnet, int start, int end) {

        return new Runnable() {

            public void run() {
                // at least on windows, this definitely doesn't do anything below 1000ms--google
                // says may differ based on os
                int timeout = 250;

                for (int i = start; i < end; i++) {
                    String host = subnet + i;
                    //System.out.println("Checking " + host + "...");
                    try {
               
                        if (InetAddress.getByName(host).isReachable(timeout)) {
                            System.out.println(host + " is reachable");
                        }
                    } catch (Exception e) {

                    }

                }

                --running;

            }

        };

    }

    public static void checkHosts(String subnet) throws IOException {
        int threadCount = 10;
        
        for (int i = 0; i < threadCount; i++) {

            Thread t = new Thread(runner(subnet, ((int)Math.ceil(255 / (double)threadCount)) * i + 1, 
                    ((int) Math.ceil(255 / (double) threadCount)) * (i + 1) + 1));
            
            t.start();
            ++running;
        }

        while(running > 0) {
            try{

                Thread.sleep(100);
            } catch(Exception e) {

            }

        }

    }

}
