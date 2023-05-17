package game.engine;

import java.util.ArrayList;

import game.Chat;
import game.Game;
import game.GameEvent;
import game.GameListener;
import game.Move;
import game.Square;

/**
 * Class used to bridge the engine and the game.
 */
public class EngineHook implements GameListener {

    private final Game game;
    private final UCIEngine engine;
    private final boolean white;
    private int depth;
    private boolean bestMove;
    private int bestMoveDepth;

    public EngineHook(UCIEngine engine, Game game, boolean white) {

        this.game = game;
        this.engine = engine;
        this.white = white;
        bestMove = true;
        bestMoveDepth = 15;
        depth = 10;

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

                if (event.getCurrIndex() < event.getPrevIndex()) {

                    if (!(event.getCurrIndex() == 0 && event.getCurr().isWhite() == white)) {
                        return;
                    }

                }

                if (event.getCurr().isWhite() != white && bestMove) {
                    try {
                        ArrayList<String> moveList = new ArrayList<>();

                        for (int i = 1; i < game.getPositions().size() - 2; i++) {
                            moveList.add(game.getPositions().get(i).getMove().toString());
                        }

                        String[] arr = new String[moveList.size()];
                        engine.setPosition(game.getPositions().get(0).toString(), moveList.toArray(arr));

                        String bm = engine.getBestMove(bestMoveDepth, game.getTimerTime(true), game.getTimerTime(false),
                                game.getSettings().getTimePerMove() * 1000, game.getSettings().getTimePerMove() * 1000);

                        Square origin = new Square(bm.substring(0, 2));
                        Square destination = new Square(bm.substring(2, 4));

                        char promoteType = bm.substring(4).equals("") ? '0'
                                : Character.toUpperCase(bm.substring(4).charAt(0));

                        if (game.getPositions().size() - 3 < 0)
                            return;

                        Move m = game.getPositions().get(game.getPositions().size() - 3).findMove(origin, destination);

                        if (m == null || !bestMove)
                            return;

                        String mn = m.getMoveNotation();

                        if (promoteType != '0')
                            mn = mn.substring(0, mn.length() - 1) + promoteType;

                        game.sendMessage(new Chat(game.getPlayer(white), System.currentTimeMillis(),
                                game.getPositions().size() > 2 &&
                                        bm.toUpperCase().startsWith(
                                                (game.getPositions().get(game.getPositions().size() - 2).getMove()
                                                        .toString().toUpperCase()))
                                                                ? "That was the best move."
                                                                : ("Best move was: " + mn)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (event.getCurr().isWhite() == white)
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
                engine.setPosition(game.getPositions().get(0).toString(), moveList.toArray(arr));

                String bm = engine.getBestMove(depth, game.getTimerTime(true), game.getTimerTime(false),
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

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isBestMove() {
        return bestMove;
    }

    public void setBestMove(boolean bestMove) {
        this.bestMove = bestMove;
    }

    public int getBestMoveDepth() {
        return bestMoveDepth;
    }

    public void setBestMoveDepth(int bestMoveDepth) {
        this.bestMoveDepth = bestMoveDepth;
    }

}
