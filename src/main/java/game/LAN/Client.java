package game.LAN;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import game.Chat;
import game.Game;
import game.GameSettings;
import game.Move;
import game.Player;
import game.Game.Reason;
import game.Game.Result;
import game.GameEvent.Type;
import game.GameEvent;
import game.GameListener;

public class Client implements GameListener {

    /** The default port to use for connecting to other Chess clients. */
    public static final int PORT = 49265;

    private long pingSent = -1;
    private long ping = -1;

    /** If the connection has been closed. */
    private boolean closed = false;

    /** The socket connection to the other client. */
    private Socket socket;

    /** The input stream. (Data coming from the other client.) */
    private BufferedReader input;

    /** The output stream. (Data sent to the other client.) */
    private PrintWriter output;

    /** The name of the other user. */
    private String name;

    /**
     * The color this user will be, based on the challenge. Should use
     * {@link #oppWhite} once game has started, as this may be
     * {@link Challenge#CHALLENGE_RANDOM}.
     */
    private int color;

    /** The settings of the game to be associated with this client. */
    private GameSettings settings;

    /** A callback to run when the game is created. */
    private Runnable gameCreatedCallback;

    /** The game associated with this client. */
    private Game game;

    /** If the opponent is white. */
    private boolean oppWhite;

    /** The thread used to ping the other client in order to measure connection. */
    private ScheduledExecutorService pingThread;

    /** The task that is executed by the {@link #pingThread}. */
    private Runnable pinger = () -> {

        pingSent = System.currentTimeMillis();

    };

    /**
     * The task that is to be run in order to listen for and process incoming data
     * from the other client.
     */
    private Runnable listener = () -> {

        try {

            String line = input.readLine();

            while (line != null) {

                receive(line);
                line = input.readLine();

            }

            input.close();
            stop();

        } catch (Exception e) {
            stop();

        }

    };

    public Client(Socket socket, String name, int color, GameSettings settings, Runnable gameCreatedCallback)
            throws Exception {

        this.socket = socket;
        this.name = name;
        this.color = color;
        this.settings = settings;
        this.gameCreatedCallback = gameCreatedCallback;

        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);

        pingThread = Executors.newScheduledThreadPool(1);
        pingThread.scheduleWithFixedDelay(pinger, 18000, 18000, TimeUnit.MILLISECONDS);

        new Thread(listener, "Game Client Listener").start();

    }

    public Client(InetAddress address, String name, int color, GameSettings settings, Runnable gameCreatedCallback)
            throws Exception {

        this.name = name;
        this.color = color;
        this.settings = settings;
        this.gameCreatedCallback = gameCreatedCallback;

        this.socket = new Socket();
        socket.connect(new InetSocketAddress(address, PORT), 5000);

        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);

        pingThread = Executors.newScheduledThreadPool(1);
        pingThread.scheduleWithFixedDelay(pinger, 18000, 18000, TimeUnit.MILLISECONDS);

        new Thread(listener, "Game Client Listener").start();

    }

    public boolean isOppWhite() {
        return oppWhite;
    }

    public Game getGame() {
        return game;
    }

    public void start() {

        send(new InitMessage(Game.VERSION, name));

    }

    public void stop() {
        stop(null);
    }

    public void stop(ErrorMessage reason) {

        try {

            if (closed)
                return;

            closed = true;
            pingThread.shutdownNow();

            if (reason != null) {

                game.sendMessage(new Chat(game.getPlayer(oppWhite),
                        (new Date().getTime()),
                        (reason.getSeverity() == ErrorMessage.FATAL ? "Fatal " : "") + "Error from "
                                + game.getPlayer(oppWhite).getName() + ": " + reason.getReason(),
                        true,
                        true));

                send(reason);

            }

            input.close();
            output.close();
            socket.close();

            if (game != null && game.getResult() == Game.Result.IN_PROGRESS)
                game.markGameOver(Game.Result.TERMINATED, Game.Reason.OTHER);

        } catch (Exception e) {

        }

    }

    @Override
    public void onPlayerEvent(GameEvent event) {

        if (event.getType() == Type.MOVE && event.getCurr().isWhite() == oppWhite) {

            try {

                send(new MoveMessage(event.getCurr().getMove().getOrigin(),
                        event.getCurr().getMove().getDestination(), event.getCurr().getMove().getPromoteType(),
                        event.getPrev().getTimerEnd()));

            } catch (Exception e) {
                stop(new ErrorMessage(ErrorMessage.FATAL, "Error sending move."));
            }

        } else if (event.getType() == Type.OVER && game.getResult() == Game.Result.TERMINATED) {

            stop(new ErrorMessage(ErrorMessage.FATAL, "Game terminated."));

        } else if (event.getType() == Type.DRAW_OFFER && game.getDrawOfferer() != null
                && game.getDrawOfferer().isWhite() != oppWhite) {

            send(Message.DRAW_OFFER);

        } else if (event.getType() == Type.OVER
                && game.getResultReason() == (oppWhite ? Game.Reason.WHITE_OFFERED_DRAW
                        : Game.Reason.BLACK_OFFERED_DRAW)) {

            send(Message.DRAW_ACCEPT);

        } else if (event.getType() == Type.OVER
                && game.getResult() == (oppWhite ? Game.Result.WHITE_WIN : Game.Result.BLACK_WIN)
                && game.getResultReason() == Game.Reason.RESIGNATION) {

            send(Message.RESIGN);

        } else if (event.getType() == Type.MESSAGE && event.getMessage() != null
                && event.getMessage().getPlayer().isWhite() != oppWhite) {

            if (!event.getMessage().isSystemMessage())
                send(new ChatMessage(new Date(event.getMessage().getTimestamp()), event.getMessage().getMessage()));

        } else if (event.getType() == Type.DRAW_DECLINED && event.isWhite() != oppWhite) {
            send(Message.DRAW_DECLINE);
        }

    }

    private void send(Message message) {

        output.println(message.toString());

    }

    private void receive(String message) {

        System.out.println(message);

        Message msg = new Message(message);

        if (msg.equals(new Message("ping"))) {

            send(new Message("pong"));

        } else if (msg.equals(new Message("pong"))) {

            ping = System.currentTimeMillis() - pingSent;

        }

        if (game == null) {

            initMessage(msg);

        } else {

            if (msg.equals(Message.STARTED) || msg.equals(Message.START)) {

                if (game.getResult() == Game.Result.NOT_STARTED) {

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

                        game.getLastPos().setTimerEnd(moveMsg.getTimerEnd());

                        send(new Message("moved", game.getLastPos().toString()));

                    } catch (Exception e) {
                        stop(new ErrorMessage(ErrorMessage.FATAL, "Invalid move: " + e.getMessage()));
                    }

                } catch (Exception e) {
                    stop(new ErrorMessage(ErrorMessage.FATAL, "Invalid move message: " + e.getMessage()));
                }

            } else if (msg.getArgs().get(0).equals("moved")) {

                if (msg.getArgs().size() != 2)
                    send(new ErrorMessage(ErrorMessage.FATAL, "Invalid moved message."));

                String fen = msg.getArgs().get(1);

                if (!fen.trim().equals(game.getLastPos().toString().trim()))
                    stop(new ErrorMessage(ErrorMessage.FATAL, "Position desynchronized."));

            } else if (msg.getArgs().get(0).equals("chat")) {

                try {

                    ChatMessage cMsg = new ChatMessage(msg.toString());
                    game.sendMessage(
                            new Chat(game.getPlayer(oppWhite),
                                    cMsg.getTimestamp().getTime(),
                                    cMsg.getMessage()));

                } catch (Exception e) {
                    send(new ErrorMessage(ErrorMessage.NORMAL, "Invalid chat message."));
                }

            } else if (msg.equals(Message.RESIGN)) {

                game.markGameOver(!oppWhite ? Game.Result.WHITE_WIN : Game.Result.BLACK_WIN,
                        Game.Reason.RESIGNATION);

            } else if (msg.equals(Message.DRAW_OFFER)) {

                try {
                    game.sendDrawOffer(oppWhite);
                } catch (Exception e) {
                    send(new ErrorMessage(ErrorMessage.NORMAL, "Cannot offer a draw right now."));
                }

            } else if (msg.equals(Message.DRAW_ACCEPT)) {

                if (game.getDrawOfferer() == null || game.getDrawOfferer().isWhite() != oppWhite) {

                    stop(new ErrorMessage(ErrorMessage.FATAL, "No draw offer was sent."));
                    return;

                }

                try {

                    game.acceptDrawOffer();

                } catch (Exception e) {
                    stop(new ErrorMessage(ErrorMessage.FATAL, "Cannot accept draw offer."));
                }

            } else if (msg.equals(Message.DRAW_DECLINE)) {

                try {
                    game.declineDrawOffer();
                } catch (Exception e) {
                    send(new ErrorMessage(ErrorMessage.NORMAL, "Unable to decline draw."));
                }

            } else if (msg.equals(ErrorMessage.TERMINATE)) {

                stop();

            } else if (msg.getArgs().get(0).equals("error")) {

                try {

                    ErrorMessage eMsg = new ErrorMessage(msg.toString());

                    game.sendMessage(new Chat(game.getPlayer(oppWhite),
                            (new Date().getTime()),
                            (eMsg.getSeverity() == ErrorMessage.FATAL ? "Fatal e" : "E") + "rror from "
                                    + game.getPlayer(oppWhite).getName() + ": " + eMsg.getReason(),
                            true,
                            true));

                    if (eMsg.getSeverity() == ErrorMessage.FATAL)
                        stop();

                } catch (Exception e) {
                    stop(new ErrorMessage(ErrorMessage.FATAL, "Invalid error message: " + e.getMessage()));
                }

            }

        }

    }

    private void initMessage(Message msg) {

        // Init message to be sent by the challenge accepter once they are ready. This
        // processes that message.
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

            boolean white = color == Challenge.CHALLENGE_WHITE;

            // Decides who is which color if the challenge was for a random color.
            if (color == Challenge.CHALLENGE_RANDOM)
                color = Math.round(Math.random()) == 0 ? Challenge.CHALLENGE_WHITE
                        : Challenge.CHALLENGE_BLACK;

            try {

                game = new Game(white ? name : iMsg.getName(),
                        !white ? name : iMsg.getName(),
                        Player.Type.HUMAN,
                        Player.Type.HUMAN,
                        new GameSettings(settings.getFen(),
                                settings.getTimePerSide(),
                                settings.getTimePerMove(),
                                settings.canPause(),
                                settings.canUndo(),
                                !white,
                                white));

                settings = game.getSettings();

            } catch (Exception e) {
                stop(new ErrorMessage(ErrorMessage.FATAL, "Unable to create game."));
                return;
            }

            game.addListener(this);

            oppWhite = color != Challenge.CHALLENGE_WHITE;

            // Tells other client the game is ready.
            send(new Message("ready",
                    (white ? (Challenge.CHALLENGE_BLACK + "")
                            : (Challenge.CHALLENGE_WHITE) + ""),
                    name,
                    settings.getFen(),
                    settings.getTimePerSide() + "",
                    settings.getTimePerMove() + ""));

            gameCreatedCallback.run();

            // Ready message to be sent by the person who created the challenge, once they
            // have processed the init message. This processes that message after receiving
            // it.
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
                        Player.Type.HUMAN,
                        Player.Type.HUMAN,
                        new GameSettings(rMsg.getFen(),
                                rMsg.getTimePerSide(),
                                rMsg.getTimePerMove(),
                                false,
                                false,
                                !white,
                                white));

                oppWhite = !white;

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

}
