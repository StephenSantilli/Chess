package game.engine;

import java.util.ArrayList;

import game.Chat;
import game.Game;
import game.GameEvent;
import game.GameListener;
import game.Square;

public class EngineHook implements GameListener {

    private final Game game;
    private final UCIEngine engine;
    private final boolean white;

    public EngineHook(UCIEngine engine, Game game, boolean white) {

        this.game = game;
        this.engine = engine;
        this.white = white;

        game.addListener(this);

    }

    @Override
    public void onPlayerEvent(GameEvent event) {

        switch (event.getType()) {
            case DRAW_DECLINED:
                break;
            case DRAW_OFFER:
                break;
            case IMPORTED:
                break;
            case MESSAGE:
                break;
            case MOVE:

                if (event.getCurrIndex() < event.getPrevIndex())
                    return;

                if (event.isWhite() == white) {
                    try {
                        ArrayList<String> moveList = new ArrayList<>();

                        for (int i = 1; i < game.getPositions().size() - 2; i++) {
                            moveList.add(game.getPositions().get(i).getMove().toString());
                        }

                        String[] arr = new String[moveList.size()];
                        engine.setPosition("startpos", moveList.toArray(arr));

                        String bm = engine.getBestMove(10, game.getTimerTime(true), game.getTimerTime(false),
                                game.getSettings().getTimePerMove() * 1000, game.getSettings().getTimePerMove() * 1000);

                        game.sendMessage(new Chat(game.getPlayer(white), System.currentTimeMillis(),
                                game.getPositions().size() > 2 &&
                                        bm.startsWith(
                                                (game.getPositions().get(game.getPositions().size() - 2).getMove()
                                                        .toString()))
                                                                ? "That was the best move."
                                                                : ("Best move was: " + bm)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    makeMove(event);

                break;
            case OVER:
                break;
            case PAUSED:
                break;
            case RESUMED:
                break;
            case STARTED:

                if (white)
                    makeMove(event);

                break;
            default:
                break;
        }

    }

    private void makeMove(GameEvent event) {

        Thread t = new Thread(() -> {

            try {

                Thread.sleep(250);

                ArrayList<String> moveList = new ArrayList<>();

                for (int i = 1; i < game.getPositions().size(); i++) {
                    moveList.add(game.getPositions().get(i).getMove().toString());
                }

                String[] arr = new String[moveList.size()];
                engine.setPosition("startpos", moveList.toArray(arr));

                String bm = engine.getBestMove(10, game.getTimerTime(true), game.getTimerTime(false),
                        game.getSettings().getTimePerMove() * 1000, game.getSettings().getTimePerMove() * 1000);
                Square origin = new Square(bm.substring(0, 2));
                Square destination = new Square(bm.substring(2, 4));
                char promoteType = bm.substring(4).equals("") ? '0' : bm.substring(4).charAt(0);

                System.out.println(bm);
                engine.waitReady();

                game.makeMove(origin, destination, promoteType);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        t.start();
    }

    public Game getGame() {
        return game;
    }

    public UCIEngine getEngine() {
        return engine;
    }

    public boolean isWhite() {
        return white;
    }

}
