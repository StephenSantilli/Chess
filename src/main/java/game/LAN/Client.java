package game.LAN;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import game.Player;
import game.Square;

public class Client {

    public static final int PORT = 49265;

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    private Player player;

    public Client(Socket socket) throws Exception {

        this.socket = socket;

        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream());

    }

    public void receive(String message) {

        if(player == null) {

            

        } else {



        }

    }

    private void makeMove(Square origin, Square destination, long timerEnd) {

    }

    public void stop() {
        try {

            input.close();
            output.close();
            socket.close();

        } catch (Exception e) {
        }

    }

    private Runnable listener = () -> {

        try {

            String line = input.readLine();

            while (line != null) {

                line = input.readLine();

            }

            input.close();

        } catch (Exception e) {

        }

    };

}
