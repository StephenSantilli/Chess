package game.PGN;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import game.Game.Result;

public class PGNMove {

    /**
     * The PGN notation text of the move, not including commentary, etc.
     * <br>
     * <br>
     * <b>Ex:</b> {@code Nf3}
     */
    private String moveText;

    private int moveNumber;

    /**
     * The {@code int} corresponding to the numeric annotation glyph. Will be the
     * same as the index in {@link #NAGs}.
     */
    private int nag;

    private ArrayList<String> comments;

    private Result termination;

    private ArrayList<ArrayList<PGNMove>> rav;

    public Result getTermination() {
        return termination;
    }

    public void setTermination(Result termination) {
        this.termination = termination;
    }

    public String getMoveText() {
        return moveText;
    }

    public int getNag() {
        return nag;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveText(String moveText) {
        this.moveText = moveText;
    }

    public void setNag(int nag) {
        this.nag = nag;
    }

    public ArrayList<ArrayList<PGNMove>> getRav() {
        return rav;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public String toString() {

        String s = "";

        s += moveText;

        if (nag > 0)
            s += " $" + nag;

        for (int x = 0; x < comments.size(); x++) {

            s += " {" + comments.get(x) + "}";

        }

        if (rav.size() > 0) {

            for (int i = 0; i < rav.size(); i++) {
                s += " (";

                ArrayList<PGNMove> r = rav.get(i);

                for (int x = 0; x < r.size(); x++) {

                    PGNMove m = r.get(x);
                    boolean black = m.getMoveNumber() % 2 != 0;

                    s += (x == 0 ? "" : " ") + (((m.getMoveNumber()) / 2) + 1) + (black ? "..." : ".");

                    s += " " + m;

                    if (!black && x + 1 < r.size()) {

                        PGNMove o = r.get(++x);

                        if (m.getComments().size() > 0 || m.getNag() != 0)
                            s += " " + (((m.getMoveNumber()) / 2) + 1) + "...";

                        s += " " + o;

                    }

                }

                s += ")";
            }

        }

        if (termination != null) {
            switch (termination) {
                case WHITE_WIN:
                    s += " 1-0";
                    break;
                case BLACK_WIN:
                    s += " 0-1";
                    break;
                case DRAW:
                    s += " 1/2-1/2";
                    break;
                case IN_PROGRESS:
                    s += " *";
                    break;
                default:
                    break;
            }
        }

        return s;

    }

    public PGNMove(String move, int moveNumber) throws Exception {

        this.moveText = move.trim();
        this.moveNumber = moveNumber;
        this.comments = new ArrayList<>();
        this.rav = new ArrayList<>();

    }

    public String getTag(String key) {

        for (int i = 0; i < comments.size(); i++) {

            Matcher m = Pattern.compile("\\[\\%(?<key>[^\\s]+)(?<value>[^\\]]+)\\]").matcher(comments.get(i));

            while (m.find()) {

                if (m.group("key").equals(key)) {
                    return m.group("value").trim().replaceAll("\n", "");
                }

            }

        }

        return null;

    }

    public long getTimerEnd() {

        Pattern pat = Pattern.compile("(?<hrs>[\\d]+):(?<mins>[\\d]+):(?<secs>[\\d]+)");
        Matcher matcher = pat.matcher(getTag("clk"));

        long timerEnd = 0;

        if (matcher.find()) {
            timerEnd += Integer.parseInt(matcher.group("hrs")) * 60 * 60 * 1000;
            timerEnd += Integer.parseInt(matcher.group("mins")) * 60 * 1000;
            timerEnd += Integer.parseInt(matcher.group("secs")) * 1000;
        } else
            timerEnd = -1;

        return timerEnd;

    }

    public static final String[] NAGs = {
            "null annotation", "good move (traditional !)", "poor move (traditional ?)",
            "very good move (traditional !!)", "very poor move (traditional ??)", "speculative move (traditional !?)",
            "questionable move (traditional ?!)", "forced move (all others lose quickly)",
            "singular move (no reasonable alternatives)", "worst move", "drawish position",
            "equal chances, quiet position", "equal chances, active position", "unclear position",
            "White has a slight advantage", "Black has a slight advantage", "White has a moderate advantage",
            "Black has a moderate advantage", "White has a decisive advantage", "Black has a decisive advantage",
            "White has a crushing advantage (Black should resign)",
            "Black has a crushing advantage (White should resign)", "White is in zugzwang", "Black is in zugzwang",
            "White has a slight space advantage", "Black has a slight space advantage",
            "White has a moderate space advantage", "Black has a moderate space advantage",
            "White has a decisive space advantage", "Black has a decisive space advantage",
            "White has a slight time (development) advantage", "Black has a slight time (development) advantage",
            "White has a moderate time (development) advantage", "Black has a moderate time (development) advantage",
            "White has a decisive time (development) advantage", "Black has a decisive time (development) advantage",
            "White has the initiative", "Black has the initiative", "White has a lasting initiative",
            "Black has a lasting initiative", "White has the attack", "Black has the attack",
            "White has insufficient compensation for material deficit",
            "Black has insufficient compensation for material deficit",
            "White has sufficient compensation for material deficit",
            "Black has sufficient compensation for material deficit",
            "White has more than adequate compensation for material deficit",
            "Black has more than adequate compensation for material deficit",
            "White has a slight center control advantage", "Black has a slight center control advantage",
            "White has a moderate center control advantage", "Black has a moderate center control advantage",
            "White has a decisive center control advantage", "Black has a decisive center control advantage",
            "White has a slight kingside control advantage", "Black has a slight kingside control advantage",
            "White has a moderate kingside control advantage", "Black has a moderate kingside control advantage",
            "White has a decisive kingside control advantage", "Black has a decisive kingside control advantage",
            "White has a slight queenside control advantage", "Black has a slight queenside control advantage",
            "White has a moderate queenside control advantage", "Black has a moderate queenside control advantage",
            "White has a decisive queenside control advantage", "Black has a decisive queenside control advantage",
            "White has a vulnerable first rank", "Black has a vulnerable first rank",
            "White has a well protected first rank", "Black has a well protected first rank",
            "White has a poorly protected king", "Black has a poorly protected king", "White has a well protected king",
            "Black has a well protected king", "White has a poorly placed king", "Black has a poorly placed king",
            "White has a well placed king", "Black has a well placed king", "White has a very weak pawn structure",
            "Black has a very weak pawn structure", "White has a moderately weak pawn structure",
            "Black has a moderately weak pawn structure", "White has a moderately strong pawn structure",
            "Black has a moderately strong pawn structure", "White has a very strong pawn structure",
            "Black has a very strong pawn structure", "White has poor knight placement",
            "Black has poor knight placement", "White has good knight placement", "Black has good knight placement",
            "White has poor bishop placement", "Black has poor bishop placement", "White has good bishop placement",
            "Black has good bishop placement", "White has poor rook placement", "Black has poor rook placement",
            "White has good rook placement", "Black has good rook placement", "White has poor queen placement",
            "Black has poor queen placement", "White has good queen placement", "Black has good queen placement",
            "White has poor piece coordination", "Black has poor piece coordination",
            "White has good piece coordination", "Black has good piece coordination",
            "White has played the opening very poorly", "Black has played the opening very poorly",
            "White has played the opening poorly", "Black has played the opening poorly",
            "White has played the opening well", "Black has played the opening well",
            "White has played the opening very well", "Black has played the opening very well",
            "White has played the middlegame very poorly", "Black has played the middlegame very poorly",
            "White has played the middlegame poorly", "Black has played the middlegame poorly",
            "White has played the middlegame well", "Black has played the middlegame well",
            "White has played the middlegame very well", "Black has played the middlegame very well",
            "White has played the ending very poorly", "Black has played the ending very poorly",
            "White has played the ending poorly", "Black has played the ending poorly",
            "White has played the ending well", "Black has played the ending well",
            "White has played the ending very well", "Black has played the ending very well",
            "White has slight counterplay", "Black has slight counterplay", "White has moderate counterplay",
            "Black has moderate counterplay", "White has decisive counterplay", "Black has decisive counterplay",
            "White has moderate time control pressure", "Black has moderate time control pressure",
            "White has severe time control pressure", "Black has severe time control pressure"
    };

}
