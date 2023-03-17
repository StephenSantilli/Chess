package game.LAN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketHandler implements Runnable {

    private Socket socket;
    private String name;

    private Client c;

    private BufferedReader input;
    private PrintWriter output;

    private Runnable inputListener = () -> {
        String line;
        try {

            line = input.readLine();

                received(line);

            

        } catch (IOException e) {

        }

    };

    private void received(String message) {

        try {
            Message msg = new Message(message);

            c.initGame(socket);
        } catch(Exception e) {
            e.printStackTrace();
        }



    }

    public SocketHandler(Socket s, String name, Client c) {

        this.socket = s;
        this.name = name;
        this.c = c;

    }

    public void run() {

        try {

            System.out.println("Connection from " + socket.getLocalAddress());

            output = new PrintWriter(socket.getOutputStream(), true);
            output.println("Chess;" + name);

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(inputListener).start();

        } catch (Exception e) {

        }

    }

    public void close() {

        try {

            input.close();
            output.close();
            socket.close();

        } catch (Exception e) {

        }

    }

}
