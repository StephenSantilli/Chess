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

        send(new InitMessage(Game.VERSION, name));

    }

    public void send(Message message) {

        output.println(message.toString());

    }

    public void receive(String message) {

        System.out.println(message);

        Message msg = new Message(message);

        if (game == null) {

            initMessage(msg);

        } else {

            if (msg.equals(Message.STARTED) || msg.equals(Message.START)) {

                if (game.getResult() == Game.RESULT_NOT_STARTED) {

                    try {

                        game.startGame();

                        if (msg.equals(Message.START))
                            send(Message.STARTED);

                    } catch (Exception e) {
                        stop(new ErrorMessage(ErrorMessage.FATAL, "Error starting game."));
                    }

                } else
                    stop(new ErrorMessage(ErrorMessage.FATAL, "Game already started."));

            } else if (msg.getArgs().get(0).equals("move")) {

                try {

                    MoveMessage moveMsg = new MoveMessage(msg.toString());

                    try {

                        game.makeMove(moveMsg.getOrigin(), moveMsg.getDestination(), moveMsg.getPromoteType());

                        game.setTimer(oppColor, moveMsg.getTimerEnd());

                    } catch (Exception e) {
                        stop(new ErrorMessage(ErrorMessage.FATAL, "Invalid move: " + e.getMessage()));
                    }

                } catch (Exception e) {
                    stop(new ErrorMessage(ErrorMessage.FATAL, "Invalid move message: " + e.getMessage()));
                }

            } else if (msg.equals(Message.TERMINATE)) {

                stop(new ErrorMessage(ErrorMessage.NORMAL, "Opponent disconnected."));

            } else if (msg.getArgs().get(0).equals("error")) {

                try {

                    ErrorMessage eMsg = new ErrorMessage(msg.toString());

                    if (eMsg.getSeverity() == ErrorMessage.FATAL)
                        stop();

                } catch (Exception e) {
                    stop(new ErrorMessage(ErrorMessage.FATAL, "Invalid error message: " + e.getMessage()));
                }

            } else if (msg.equals(Message.DRAW_OFFER)) {

                try {
                    game.sendDrawOffer(oppColor);
                } catch (Exception e) {
                    send(new ErrorMessage(ErrorMessage.NORMAL, "Cannot offer a draw right now."));
                }

            } else if (msg.equals(Message.DRAW_ACCEPT)) {

                if (game.getLastPos().getDrawOfferer() != (oppColor ? 2 : 1)) {

                    stop(new ErrorMessage(ErrorMessage.FATAL, "No draw offer was sent."));
                    return;

                }

                try {

                    game.acceptDrawOffer();

                } catch (Exception e) {
                    stop(new ErrorMessage(ErrorMessage.FATAL, "Cannot accept draw offer."));
                }

            } else if (msg.equals(Message.RESIGN)) {

                game.markGameOver(!oppColor ? Game.RESULT_WHITE_WIN : Game.RESULT_BLACK_WIN,
                        Game.REASON_RESIGNATION);

            }

        }

    }

    private void initMessage(Message msg) {

        if (msg.getArgs().get(0).equals("init")) {

            InitMessage iMsg = null;
            try {

                iMsg = new InitMessage(msg.toString());

            } catch (Exception e) {

                stop(new ErrorMessage(ErrorMessage.FATAL, "Invalid init message: " + e.getMessage()));
                return;

            }

            if (!iMsg.getVersion().equals(Game.VERSION)) {
                stop(new ErrorMessage(ErrorMessage.FATAL, "Version mismatch."));
                return;
            }

            if (color == Challenge.CHALLENGE_RANDOM)
                color = Math.round(Math.random()) == 0 ? Challenge.CHALLENGE_WHITE
                        : Challenge.CHALLENGE_BLACK;

            game = new Game(color == Challenge.CHALLENGE_WHITE ? name : iMsg.getName(),
                    color == Challenge.CHALLENGE_BLACK ? name : iMsg.getName(),
                    settings);

            game.addListener(this);

            oppColor = color != Challenge.CHALLENGE_WHITE;

            send(new Message("ready",
                    (color == Challenge.CHALLENGE_WHITE ? (Challenge.CHALLENGE_BLACK + "")
                            : (Challenge.CHALLENGE_WHITE) + ""),
                    name,
                    settings.getTimePerSide() + "",
                    settings.getTimePerMove() + ""));

            gameCreatedCallback.run();

        } else if (msg.getArgs().get(0).equals("ready")) {

            ReadyMessage rMsg = null;
            try {
                rMsg = new ReadyMessage(msg.toString());
            } catch (Exception e) {
                stop(new ErrorMessage(ErrorMessage.FATAL, "Invalid ready message."));
                return;
            }

            boolean white = rMsg.getOppColor() == Challenge.CHALLENGE_WHITE;
            try {

                game = new Game(white ? name : rMsg.getName(),
                        !white ? name : rMsg.getName(),
                        new GameSettings(rMsg.getTimePerSide(), rMsg.getTimePerMove(), false, false, !white, white));
                oppColor = !white;
                game.addListener(this);

            } catch (Exception e) {

                stop(new ErrorMessage(ErrorMessage.FATAL, "Unable to initialize game."));

                return;
            }

            send(new Message("start"));

            gameCreatedCallback.run();

        } else {
            stop(new ErrorMessage(ErrorMessage.FATAL, "Intialization/ready message expected."));
        }

    }

    public void stop() {
        stop(null);
    }

    public void stop(Message reason) {

        try {

            System.out.println("Stopping because: " + reason);

            if (reason != null)
                send(reason);

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
                stop(new ErrorMessage(ErrorMessage.FATAL, "Error sending move."));
            }

        } else if (event.getType() == GameEvent.TYPE_OVER && game.getResult() == Game.RESULT_TERMINATED) {

            stop(new ErrorMessage(ErrorMessage.FATAL, "Game terminated."));

        } else if (event.getType() == GameEvent.TYPE_DRAW_OFFER && game.getLastPos().getDrawOfferer() != 0
                && (game.getLastPos().getDrawOfferer() == 1) != oppColor) {

            send(Message.DRAW_OFFER);

        } else if (event.getType() == GameEvent.TYPE_OVER
                && game.getResultReason() == (oppColor ? Game.REASON_WHITE_OFFERED_DRAW
                        : Game.REASON_BLACK_OFFERED_DRAW)) {

            send(Message.DRAW_ACCEPT);

        } else if (event.getType() == GameEvent.TYPE_OVER
                && game.getResult() == (oppColor ? Game.RESULT_WHITE_WIN : Game.RESULT_BLACK_WIN)
                && game.getResultReason() == Game.REASON_RESIGNATION) {

            send(Message.RESIGN);

        }

    }

}
