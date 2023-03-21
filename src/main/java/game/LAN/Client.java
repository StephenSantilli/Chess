package game.LAN;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import game.Game;
import game.GameSettings;
import game.Player;
import game.GameEvent;
import game.GameListener;
import game.Square;

public class Client implements GameListener {

    public static final int PORT = 49265;

    private Socket socket;
    
    private BufferedReader input;
    private PrintWriter output;

    private String name;
    private int color;
    private GameSettings settings;

    private Runnable gameCreatedCallback;

    private Game game;
    private boolean oppColor;

    public boolean isOppColor() {
        return oppColor;
    }

    public Game getGame() {
        return game;
    }

    private Runnable listener = () -> {

        try {

            String line = input.readLine();

            while (line != null) {

                receive(line);
                line = input.readLine();

            }

            input.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    };

    public Client(Socket socket, String name, int color, GameSettings settings, Runnable gameCreatedCallback)
            throws Exception {

        this.socket = socket;
        this.name = name;
        this.color = color;
        this.settings = settings;
        this.gameCreatedCallback = gameCreatedCallback;

        try {

            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);

            new Thread(listener, "Game Client Listener").start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendInitMessage() {

        send(new Message("init", Game.VERSION, name));

    }

    public void send(Message message) {

        output.println(message.toString());

    }

    public void receive(String message) {

        System.out.println(message);

        Message msg = new Message(message);
        ArrayList<String> a = msg.getArgs();

        if (game == null) {

            initMessage(a);

        } else {

            if (msg.equals(Message.STARTED)) {

                if (game.getResult() == Game.RESULT_NOT_STARTED) {

                    try {
                        game.startGame();
                    } catch (Exception e) {
                        stop(true, "Error starting game.", false);
                    }

                } else
                    stop(true, "Game already started.", false);

            } else if (msg.equals(Message.START)) {

                if (game.getResult() == Game.RESULT_NOT_STARTED) {

                    try {
                        game.startGame();
                        send(Message.STARTED);
                    } catch (Exception e) {
                        stop(true, "Error starting game.", false);
                    }

                } else
                    stop(true, "Game already started.", false);

            } else if (a.get(0).equals("move")) {

                if (a.size() < 5)
                    stop(true, "Invalid move format.", false);

                Square origin = new Square(a.get(1));
                Square destination = new Square(a.get(2));

                char promoteType = a.get(3).charAt(0);

                long timerEnd = -1;
                if (game.getSettings().getTimePerSide() > 0) {
                    if (a.size() >= 5)
                        timerEnd = Long.parseLong(a.get(4));
                    else {
                        stop(true, "Move timer not sent.", false);
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

                    game.makeMove(origin, destination, promoteType);

                    game.setTimer(oppColor, timerEnd);

                } catch (Exception e) {
                    stop(true, "Invalid move. " + e.getMessage(), false);
                }

            } else if (msg.equals(Message.TERMINATE)) {
                stop(false, "Opponent disconnected.", false);
            } else if (a.get(0).equals("error") && a.get(1).equals("fatal")) {
                stop(false, a.get(2), false);
            } else if (msg.equals(Message.DRAW_OFFER)) {

                try {
                    game.sendDrawOffer(oppColor);
                } catch (Exception e) {
                    send(new Message("error", "normal", "Cannot offer a draw right now."));
                }

            } else if (msg.equals(Message.DRAW_ACCEPT)) {

                if (game.getLastPos().getDrawOfferer() != (oppColor ? 2 : 1)) {

                    send(new Message("error", "normal", "No draw offer was sent."));
                    return;

                }
                try {

                    game.acceptDrawOffer();

                } catch (Exception e) {
                    stop(true, "Cannot accept draw offer.", false);
                }

            } else if (msg.equals(Message.RESIGN)) {

                game.markGameOver(!oppColor ? Game.RESULT_WHITE_WIN : Game.RESULT_BLACK_WIN,
                        Game.REASON_RESIGNATION);

            }

        }

    }

    private void initMessage(ArrayList<String> a) {

        if (a.get(0).equals("init")) {

            if (a.get(1).equals(Game.VERSION) && a.get(2).matches(Player.NAME_REGEX)) {

                if (color == Challenge.CHALLENGE_RANDOM)
                    color = Math.round(Math.random()) == 0 ? Challenge.CHALLENGE_WHITE
                            : Challenge.CHALLENGE_BLACK;

                game = new Game(color == Challenge.CHALLENGE_WHITE ? name
                        : a.get(
                                2),
                        color == Challenge.CHALLENGE_BLACK ? name : a.get(2), settings);

                game.addListener(this);

                oppColor = color != Challenge.CHALLENGE_WHITE;

                send(new Message("ready",
                        (color == Challenge.CHALLENGE_WHITE ? (Challenge.CHALLENGE_BLACK + "")
                                : (Challenge.CHALLENGE_WHITE) + ""),
                        name,
                        settings.getTimePerSide() + "",
                        settings.getTimePerMove() + ""));

                gameCreatedCallback.run();

            } else {

                if (!a.get(1).equals(Game.VERSION))
                    stop(true, "Version mismatch", false);
                else if (!a.get(2).matches(Player.NAME_REGEX))
                    stop(true, "Invalid name.", false);

            }

        } else if (a.get(0).equals("ready")) {

            if (a.size() != 5)
                stop(true, "Invalid argument length.", false);
            else if (!a.get(1).equals(Challenge.CHALLENGE_WHITE + "")
                    && !a.get(1).equals(Challenge.CHALLENGE_BLACK + ""))
                stop(true, "Invalid color.", false);
            else if (!a.get(2).matches(Player.NAME_REGEX))
                stop(true, "Invalid name.", false);
            else {

                long timePerSide, timePerMove;
                try {
                    timePerSide = Long.parseLong(a.get(3));
                    timePerMove = Long.parseLong(a.get(4));
                } catch (Exception e) {
                    stop(true, "Invalid time per side or time per move.", false);
                    return;
                }

                boolean white = a.get(1).equals(Challenge.CHALLENGE_WHITE + "");
                try {
                    game = new Game(white ? name
                            : a.get(
                                    2),
                            !white ? name
                                    : a.get(
                                            2),
                            new GameSettings(timePerSide, timePerMove, false, false, !white, white));
                    oppColor = !white;
                    game.addListener(this);

                } catch (Exception e) {
                    stop(true, "Unable to initialize game.", false);

                    return;
                }

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

            System.out.println("Stopping because: " + reason);

            if (send && !normal)
                send(new Message("error", "fatal", reason));
            else if (send)
                send(Message.TERMINATE);

            input.close();
            output.close();
            socket.close();

            if (game != null && game.getResult() == Game.RESULT_IN_PROGRESS)
                game.markGameOver(Game.RESULT_TERMINATED, Game.REASON_OTHER);

        } catch (Exception e) {

        }

    }

    @Override
    public void onPlayerEvent(GameEvent event) {

        if (event.getType() == GameEvent.TYPE_MOVE && event.getCurr().isWhite() == oppColor) {
            try {

                send(new MoveMessage(event.getCurr().getMove().getOrigin(),
                        event.getCurr().getMove().getDestination(), event.getCurr().getMove().getPromoteType(),
                        event.getPrev().getTimerEnd()));

            } catch (Exception e) {
                stop(true, "Move error.", false);
            }

        } else if (event.getType() == GameEvent.TYPE_OVER && game.getResult() == Game.RESULT_TERMINATED) {

            stop(true, "Game terminated.", false);

        } else if (event.getType() == GameEvent.TYPE_DRAW_OFFER && game.getLastPos().getDrawOfferer() != 0
                && (game.getLastPos().getDrawOfferer() == 1) != oppColor) {

            send(Message.DRAW_OFFER);

        } else if (event.getType() == GameEvent.TYPE_OVER
                && game.getResultReason() == (oppColor ? Game.REASON_WHITE_OFFERED_DRAW
                        : Game.REASON_BLACK_OFFERED_DRAW)) {

            send(Message.DRAW_ACCEPT);

        } else if (event.getType() == GameEvent.TYPE_OVER
                && game.getResult() == (!oppColor ? Game.RESULT_WHITE_WIN : Game.RESULT_BLACK_WIN)
                && game.getResultReason() == Game.REASON_RESIGNATION) {

            send(Message.RESIGN);

        }

    }

}
