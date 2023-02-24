package PGNParser;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PGNMove {

    /**
     * The PGN notation text of the move, not including commentary, etc.
     * <br>
     * <br>
     * <b>Ex:</b> {@code Nf3}
     */
    private String moveText;

    /** The commentary on the move. May be {@code null}. */
    private String commentary;

    /**
     * Suffix for commentary. Matches up with the first six (excluding 0)
     * {@link #NAGs}
     * <br>
     * <br>
     * <b>Potential values:</b>
     * <ul>
     * <li>!
     * <li>?
     * <li>!!
     * <li>!?
     * <li>??
     */
    private String suffix;

    /** The result of the game. See {@link PGNParser#result}. */
    private String gameTermination;

    /** The {@code int} corresponding to the numeric annotation glyph. Will be the same as the index in {@link #NAGs}. */
    private int NAG;

    public String getTag(String key) {

        Matcher m = Pattern.compile("\\[\\%(?<key>[^\\s]+) (?<value>[^\\]]+)\\]").matcher(commentary);
        
        while(m.find()) {

            if(m.group("key").equals(key)) {
                return m.group("value").trim().replaceAll("\n", "");
            }

        }

        return null;

    }

    public String toString() {

        String str = "";

        str += moveText;
        str += suffix;
        if(NAG > 5 || (suffix.equals("") && NAG > 0)) str += " $" + NAG;
        if(!commentary.equals("")) str += " " + commentary;
        if(!gameTermination.equals("")) str += " " + gameTermination;

        return str;

    }

    public PGNMove(String move, String comment, String NAG, String result, String suffix) throws Exception {

        this.moveText = move;

        this.commentary = comment == null ? "" : comment.trim().replaceAll("\n", " ").replaceAll("  ", " ");

        this.NAG = NAG == null ? 0 : Integer.parseInt(NAG.substring(1).trim());

        this.gameTermination = result == null ? "" : result.trim().replaceAll("\n", " ").replaceAll("  ", " ");

        this.suffix = suffix == null ? "" : suffix.trim().replaceAll("\n", " ").replaceAll("  ", " ");

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

    public String getMoveText() {
        return moveText;
    }

    public String getCommentary() {
        return commentary;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getGameTermination() {
        return gameTermination;
    }

    public int getNAG() {
        return NAG;
    }

    public static String[] getNags() {
        return NAGs;
    }

}
