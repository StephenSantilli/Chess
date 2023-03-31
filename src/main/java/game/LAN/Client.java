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
import game.Player;
import game.GameEvent;
import game.GameListener;

public class Client implements GameListener {

    public static final int PORT = 49265;

    private long pingSent = -1;
    private long ping = -1;

    private boolean closed = false;

    private Socket socket;

    private BufferedReader input;
    private PrintWriter output;

    private String name;
    private int color;
    private GameSettings settings;

    private Runnable gameCreatedCallback;

    private Game game;
    private boolean oppColor;

    private ScheduledExecutorService pingThread;
    private Runnable pinger = () -> {

        pingSent = System.currentTimeMillis();
        send(new Message("ping"));

    };

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

    public boolean isOppColor() {
        return oppColor;
    }

    public Game getGame() {
        return game;
    }

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

            pingThread = Executors.newScheduledThreadPool(1);
            pingThread.scheduleWithFixedDelay(pinger, 18000, 18000, TimeUnit.MILLISECONDS);

            new Thread(listener, "Game Client Listener").start();

        } catch (Exception e) {
            stop(new ErrorMessage(ErrorMessage.FATAL, "Unable to establish connection."));
        }

    }

    public Client(InetAddress address, String name, int color, GameSettings settings, Runnable gameCreatedCallback)
            throws Exception {

        this.name = name;
        this.color = color;
        this.settings = settings;
        this.gameCreatedCallback = gameCreatedCallback;

        try {

            this.socket = new Socket();
            socket.connect(new InetSocketAddress(address, PORT));

            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);

            pingThread = Executors.newScheduledThreadPool(1);
            pingThread.scheduleWithFixedDelay(pinger, 18000, 18000, TimeUnit.MILLISECONDS);

            new Thread(listener, "Game Client Listener").start();

        } catch (Exception e) {
            stop(new ErrorMessage(ErrorMessage.FATAL, "Unable to establish connection."));
        }

    }

    public void start() {

        send(new InitMessage(Game.VERSION, name));

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

            System.out.println(ping + "ms");

        }

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

                        game.getLastPos().setTimerEnd(moveMsg.getTimerEnd());
                        ;
                        // game.setTimer(oppColor, moveMsg.getTimerEnd());

                        send(new Message("moved", game.getLastPos().toString()));

                    } catch (Exception e) {
                        stop(new ErrorMessage(ErrorMessage.FATAL, "Invalid move: " + e.getMessage()));
                    }

                } catch (Exception e) {
                    stop(new ErrorMessage(ErrorMessage.FATAL, "Invalid move message: " + e.getMessage()));
                }

            } else if (msg.equals(ErrorMessage.TERMINATE)) {

                stop();

            } else if (msg.getArgs().get(0).equals("error")) {

                try {

                    ErrorMessage eMsg = new ErrorMessage(msg.toString());

                    game.sendMessage(new Chat(game.getPlayer(oppColor), (new Date().getTime()),
                            (eMsg.getSeverity() == ErrorMessage.FATAL ? "Fatal " : "") + "Error from "
                                    + game.getPlayer(oppColor).getName() + ": " + eMsg.getReason(),
                            true, true));

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

            } else if (msg.getArgs().get(0).equals("chat")) {

                try {
                    ChatMessage cMsg = new ChatMessage(msg.toString());
                    game.sendMessage(
                            new Chat(game.getPlayer(oppColor), cMsg.getTimestamp().getTime(), cMsg.getMessage()));
                } catch (Exception e) {
                    send(new ErrorMessage(ErrorMessage.NORMAL, "Invalid chat message."));
                }

            } else if (msg.getArgs().get(0).equals("moved")) {

                if (msg.getArgs().size() != 2)
                    send(new ErrorMessage(ErrorMessage.FATAL, "Invalid moved message."));

                String fen = msg.getArgs().get(1);

                if (!fen.trim().equals(game.getLastPos().toString().trim()))
                    stop(new ErrorMessage(ErrorMessage.FATAL, "Position desynchronized."));

            } else if (msg.equals(Message.DRAW_DECLINE)) {

                try {
                    game.declineDrawOffer();
                } catch (Exception e) {
                    send(new ErrorMessage(ErrorMessage.NORMAL, "Unable to decline draw."));
                }

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

            try {
                game = new Game(color == Challenge.CHALLENGE_WHITE ? name : iMsg.getName(),
                        color == Challenge.CHALLENGE_BLACK ? name : iMsg.getName(),
                        Player.HUMAN,
                        Player.HUMAN,
                        settings);
            } catch (Exception e) {
                stop(new ErrorMessage(ErrorMessage.FATAL, "Unable to create game."));
            }

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
                        Player.HUMAN,
                        Player.HUMAN,
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

    public void stop(ErrorMessage reason) {

        try {
            if (closed)
                return;

            closed = true;
            pingThread.shutdownNow();

            if (reason != null) {
                game.sendMessage(new Chat(game.getPlayer(oppColor), (new Date().getTime()),
                        (reason.getSeverity() == ErrorMessage.FATAL ? "Fatal " : "") + "Error from "
                                + game.getPlayer(oppColor).getName() + ": " + reason.getReason(),
                        true, true));
                send(reason);
            }

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

        } else if (event.getType() == GameEvent.TYPE_MESSAGE && event.getMessage() != null
                && event.getMessage().getPlayer().isWhite() != oppColor) {

            if (!event.getMessage().isSystemMessage())
                send(new ChatMessage(new Date(event.getMessage().getTimestamp()), event.getMessage().getMessage()));

        } else if (event.getType() == GameEvent.TYPE_DRAW_DECLINED && event.isWhite() != oppColor) {
            send(Message.DRAW_DECLINE);
        }

    }

}
