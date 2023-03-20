package game.LAN;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import game.Game;
import game.GameSettings;
import game.Player;
import game.Square;

public class Client {

    public static final int PORT = 49265;

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    private String name;
    private GameSettings settings;
    private int color;
    private Runnable gameCreatedCallback;

    private Player player;

    public Player getPlayer() {
        return player;
    }

    public Client(Socket socket, String name, int color, GameSettings settings, Runnable gameCreatedCallback) throws Exception {

        this.socket = socket;
        this.name = name;
        this.color = color;
        this.settings = settings;
        this.gameCreatedCallback = gameCreatedCallback;

        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream());

        new Thread(listener).start();

    }

    public void sendInitMessage() {

        send(new Message("init", Game.VERSION, name));

    } 

    public void send(Message message) {

        output.println(message.toString());

    }

    public void receive(String message) {

        Message msg = new Message(message);
        String[] a = msg.getArgs();

        if (player == null) {

            initMessage(a);

        } else {

            if (a[0].equals("started")) {

                if (player.getGame().getResult() == Game.RESULT_NOT_STARTED) {

                    try {
                        player.getGame().startGame();
                    } catch (Exception e) {
                        stop(true, "Error starting game.", false);
                    }

                } else
                    stop(true, "Game already started.", false);

            } else if (a[0].equals("start")) {

                if (player.getGame().getResult() == Game.RESULT_NOT_STARTED) {

                    try {
                        player.getGame().startGame();
                        send(new Message("started"));
                    } catch (Exception e) {
                        stop(true, "Error starting game.", false);
                    }

                } else
                    stop(true, "Game already started.", false);

            } else if (a[0].equals("move")) {

                Square origin = new Square(a[1]);
                Square destination = new Square(a[2]);

                long timerEnd = -1;
                if (player.getGame().getSettings().getTimePerSide() > 0) {
                    if (a.length >= 4)
                        timerEnd = Long.parseLong(a[3]);
                    else {
                        stop(true, "Move timer not sent.", true);
                        return;
                    }
                }

                if (!origin.isValid()) {
                    stop(true, "Origin square invalid.", true);
                    return;
                }

                if (!destination.isValid()) {
                    stop(true, "Destination square invalid.", true);
                }
                
                try {

                    player.makeMove(origin, destination);

                    player.getGame().setTimer(player.isWhite(), timerEnd);

                } catch (Exception e) {
                    stop(true, "Invalid move.", true);
                }

            } else if (a[0].equals("terminate")) {
                stop(false, "Opponent disconnected.", false);
            } else if (a[0].equals("error") && a[1].equals("fatal")) {
                stop(false, a[2], false);
            }

        }

    }

    private void initMessage(String[] a) {

        if (a[0].equals("init")) {

            if (a[1].equals(Game.VERSION) && a[2].matches(Player.NAME_REGEX)) {

                if (color == Challenge.CHALLENGE_RANDOM)
                    color = Math.round(Math.random()) == 0 ? Challenge.CHALLENGE_WHITE
                            : Challenge.CHALLENGE_BLACK;

                Game game = new Game(color == Challenge.CHALLENGE_WHITE ? name : a[2],
                        color == Challenge.CHALLENGE_BLACK ? name : a[2], settings);

                player = game.getPlayer(color != Challenge.CHALLENGE_WHITE);

                send(new Message("ready",
                        color == Challenge.CHALLENGE_WHITE ? Challenge.CHALLENGE_BLACK + ""
                                : Challenge.CHALLENGE_WHITE + "",
                        name,
                        settings.getTimePerSide() + "",
                        settings.getTimePerMove() + ""));

                gameCreatedCallback.run();
                
            } else {

                if (!a[1].equals(Game.VERSION))
                    stop(true, "Version mismatch", false);
                else if (!a[2].matches(Player.NAME_REGEX))
                    stop(true, "Invalid name.", false);

            }

        } else if (a[0].equals("ready")) {

            if (a.length != 5)
                stop(true, "Invalid argument length.", false);
            else if (!a[1].equals(Challenge.CHALLENGE_WHITE + "") && !a[1].equals(Challenge.CHALLENGE_BLACK + ""))
                stop(true, "Invalid color.", false);
            else if (!a[2].matches(Player.NAME_REGEX))
                stop(true, "Invalid name.", false);
            else {
                long timePerSide, timePerMove;
                try {
                    timePerSide = Long.parseLong(a[3]);
                    timePerMove = Long.parseLong(a[4]);
                } catch (Exception e) {
                    stop(true, "Invalid time per side or time per move.", false);
                    return;
                }

                boolean white = a[1].equals(Challenge.CHALLENGE_WHITE + "");
                Game game;
                try {
                    game = new Game(white ? name : a[2],
                            !white ? name : a[2],
                            new GameSettings(timePerSide, timePerMove, false, false, !white, white));
                } catch (Exception e) {
                    stop(true, "Unable to initialize game.", false);
                    return;
                }

                player = game.getPlayer(color != Challenge.CHALLENGE_WHITE);

                send(new Message("start"));

                gameCreatedCallback.run();

            }

        } else {
            send(new Message("error", "normal", "Initialization/ready message expected."));
        }

    }

    public void stop() {
        stop(false, null, false);
    }

    public void stop(boolean send, String reason, boolean normal) {

        try {

            if (send && !normal)
                send(new Message("error", "fatal", reason));
            else if (send)
                send(new Message("terminate"));

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
