package game.LAN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import game.Game;
import game.GameListener;
import game.Move;
import game.Player;

public class LANPlayer implements Runnable, GameListener, Player {

    private Socket conn;
    private String name;
    private Game game;

    private Thread listener;

    private PrintWriter output;
    private BufferedReader input;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Game getGame() {
        return game;
    }

    public LANPlayer(String name, Socket conn, Game game) {

        this.name = name;
        this.conn = conn;
        this.game = game;

        try {
            output = new PrintWriter(conn.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stop() {

        try {
            output.println(new Message(name, "x"));
            output.close();
            input.close();
            conn.close();
        } catch (Exception e) {

        }

    }

    public void start() {
        try {

            output.println(new Message(name, "-"));
            game.startGame(this);
            listener = new Thread(this);
            listener.start();

        } catch (Exception e) {

        }

    }

    public void received(String message) {

        try {
            Message msg = new Message(message);
            if (msg.getMoveText().length() > 1) {
                game.makeMove(new Move(msg.getMoveText(), game.getLastPos(), game.isWhiteTurn(true)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(Message msg) {

        output.println(msg);

    }

    public void run() {

        try {
            String l = input.readLine();
            while (l != null) {
                received(l);
                l = input.readLine();
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void moveMade() {

        sendMessage(new Message(name, game.getLastPos().getMoveString()));

    }

    @Override
    public void undoMove() {
        // TODO Auto-generated method stub

    }

    @Override
    public void redoMove() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetMoves() {
        // TODO Auto-generated method stub

    }

    @Override
    public void posChanged(int old, int curr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void gameOver() {
        // TODO Auto-generated method stub

    }

    @Override
    public void timerChange() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pauseGame() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resumeGame() {
        // TODO Auto-generated method stub

    }

}
