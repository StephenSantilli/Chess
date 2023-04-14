package game.PGN;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import game.Game;
import game.Position;
import game.Game.Result;

/**
 * Class to parse a .pgn file following this specification:
 * http://www.saremba.de/chessgml/standards/pgn/pgn-complete.htm
 */
public class PGNParser {

    public static void main(String[] args) throws Exception {

        Scanner s = new Scanner(new FileReader("./test pgns/Untitled.pgn"));
        String str = "";
        while (s.hasNextLine()) {
            str += s.nextLine().trim() + "\n";
        }
        PGNParser p = new PGNParser(str);

        System.out.println(p.outputPGN(true));

    }

    /** The content of the PGN file. */
    private String text;

    /** A list of all of the {@code String} moves in the PGN text. */
    private ArrayList<String> moves;

    /** The parsed moves. */
    private ArrayList<PGNMove> parsedMoves;

    /** Used for any extra, non-standard tags. */
    private Map<String, String> tags;

    private String termination;

    public Map<String, String> getTags() {
        return tags;
    }

    public String getText() {
        return text;
    }

    public ArrayList<String> getMoves() {
        return moves;
    }

    public ArrayList<PGNMove> getParsedMoves() {
        return parsedMoves;
    }

    public PGNParser(String text) throws Exception {

        // this.text = String.join(" ", text.trim().split("\n"));
        this.text = text.trim();
        tags = new HashMap<String, String>();
        moves = new ArrayList<String>();
        parsedMoves = new ArrayList<PGNMove>();

        parse();

    }

    public long getTimePerSide() {

        final String tc = tags.getOrDefault("TimeControl", "-");
        if (!tc.matches("[\\d]+(\\+[\\d]+)?"))
            return 0;

        final String[] split = tc.split("\\+");

        try {
            return Integer.parseInt(split[0]);
        } catch (Exception e) {
            return 0;
        }

    }

    public long getTimePerMove() {

        final String tc = tags.getOrDefault("TimeControl", "-");
        if (!tc.matches("[\\d]+(\\+[\\d]+)?"))
            return 0;

        final String[] split = tc.split("\\+");

        try {
            return Integer.parseInt(split[1]);
        } catch (Exception e) {
            return 0;
        }

    }

    public static String millisToOutputFormat(long time) {

        long hours = (time / 1000 / 60 / 60);
        long minutes = (time / 1000 / 60 % 60);
        long seconds = (time / 1000 % 60 % 60 % 60);

        String s = "";

        s += hours + ":";

        if (minutes < 10) {
            s += "0";
        }
        s += minutes + ":";

        if (seconds < 10) {
            s += "0";
        }
        s += seconds;

        return s;

    }

    public PGNParser(Game game, Map<String, String> tags, boolean includeClock) throws Exception {

        text = "";
        this.tags = tags;
        moves = new ArrayList<String>();
        parsedMoves = new ArrayList<PGNMove>();

    }

    public String outputPGN(boolean includeTags) {

        String str = "";

        if (includeTags) {

            str += "[Event \"" + tags.getOrDefault("Event", "?") + "\"]\n";
            str += "[Site \"" + tags.getOrDefault("Site", "?") + "\"]\n";
            str += "[Date \"" + tags.getOrDefault("Date", "????.??.??") + "\"]\n";
            str += "[Round \"" + tags.getOrDefault("Round", "-") + "\"]\n";
            str += "[White \"" + tags.getOrDefault("White", "?") + "\"]\n";
            str += "[Black \"" + tags.getOrDefault("Black", "?") + "\"]\n";
            str += "[Result \"" + tags.getOrDefault("Result", "*") + "\"]\n";

            for (Map.Entry<String, String> tag : tags.entrySet()) {

                if (tag.getKey().equals("Event") || tag.getKey().equals("Site") || tag.getKey().equals("Date")
                        || tag.getKey().equals("Round") || tag.getKey().equals("White") || tag.getKey().equals("Black")
                        || tag.getKey().equals("Result"))
                    continue;

                str += "[" + tag.getKey() + " \"" + tag.getValue() + "\"]\n";

            }

            str += "\n";

        }

        String moveList = "";
        for (int i = 0; i < parsedMoves.size(); i++) {

            PGNMove m = parsedMoves.get(i);

            moveList += " " + ((i / 2) + 1) + ".";

            moveList += " " + outputMove(m);

            if (i + 1 < parsedMoves.size()) {

                PGNMove o = parsedMoves.get(++i);

                if (parsedMoves.get(i).getComments().size() > 0 ||
                        parsedMoves.get(i).getNAG() != 0)
                    moveList += " " + ((i / 2) + 1) + "...";

                moveList += " " + outputMove(o);

            }

        }

        while (moveList.length() > 80) {

            int find = moveList.lastIndexOf(" ", 80);

            if (find <= -1)
                find = 79;

            str += moveList.substring(0, find + 1).trim() + "\n";
            moveList = moveList.substring(find + 1);

        }
        str += moveList;
        str += " " + termination;
        str += "\n";

        return str;
    }

    private String outputMove(PGNMove move) {

        // TODO: move this to the tostring
        String s = "";

        s += move;

        if (move.getNAG() > 0)
            s += " $" + move.getNAG();

        for (int x = 0; x < move.getComments().size(); x++) {

            s += " {" + move.getComments().get(x) + "}";

        }

        if (move.getRav().size() > 0) {

            s += " (";

            for (int x = 0; x < move.getRav().size(); x++) {

                PGNMove m = move.getRav().get(x);

                s += (x == 0 ? "" : " ") + (((parsedMoves.size() - 1 + x) / 2) + 1) + ".";

                s += " " + m;

                if (x + 1 < move.getRav().size()) {

                    PGNMove o = move.getRav().get(++x);

                    if (o.getComments().size() > 0 || o.getNAG() != 0)
                        s += " " + (((parsedMoves.size() - 1 + x) / 2) + 1) + "...";

                    s += " " + outputMove(o);

                }

                // s += outputMove(move.getRav().get(x));

            }

            s += ")";

        }

        return s;

    }

    private static final String TOKEN_REGEX = "(?<termination>1-0|0-1|1/2-1/2|\\*)|(?<num>[\\d]+)|(?<period>\\.)|\\[|\\]|\\(|\\)|\\<|\\>|(?<nag>\\$[\\d]+)|(?<symbol>[A-Za-z0-9][A-Za-z0-9_+#=:\\-]*)|(?<str>\"[^\"]*\")|(?<comment>\\{[^}]*\\})|(?<eol>\\;([^\\n]*))|(?<suffix>[?!]{1,2})";

    private void parse() throws Exception {

        Matcher t = Pattern.compile(TOKEN_REGEX).matcher(text);
        int ravDepth = 0;

        while (t.find()) {

            String tok = t.group();

            if (tok.equals("[")) {

                if (t.find()) {

                    String key = t.group();

                    if (t.find()) {

                        String value = String.join("", t.group().split("\""));

                        if (t.find()) {

                            if (t.group().equals("]")) {

                                tags.put(key, value);

                            } else {
                                throw new Exception("Tag @ " + t.start() + " not closed.");
                            }

                        } else {
                            throw new Exception("Tag @ " + t.start() + " not closed.");
                        }

                    } else {
                        throw new Exception("Tag and key found @ " + t.start() + ", but no value found.");
                    }

                } else {
                    throw new Exception("Tag started @ " + t.start() + ", but no key found.");
                }

            } else if (tok.matches("1-0|0-1|1/2-1/2|\\*")) {

                termination = tok;
                // switch (tok) {
                // case "1-0":
                // termination = Result.WHITE_WIN;
                // break;
                // case "0-1":
                // termination = Result.BLACK_WIN;
                // break;
                // case "1/2-1/2":
                // termination = Result.DRAW;
                // break;
                // case "*":
                // termination = Result.IN_PROGRESS;
                // }

                // Comments
            } else if (tok.matches("\\{[^}]*\\}|\\;([^\\n]*)")) {

                if (parsedMoves.size() == 0)
                    throw new Exception("Error @ " + t.start() + ". Comment given before a move.");

                boolean eol = tok.startsWith(";");
                String comment = eol ? tok.substring(1) : tok.substring(1, tok.length() - 1);

                getLast(ravDepth).get(getLast(ravDepth).size() - 1).getComments().add(comment.replaceAll("\n", " "));

                // NAGs
            } else if (tok.matches("\\$[\\d]+")) {

                if (getLast(ravDepth).size() == 0)
                    throw new Exception("Error @ " + t.start() + ". NAG given before a move.");

                try {
                    getLast(ravDepth).get(getLast(ravDepth).size() - 1).setNAG(Integer.parseInt(tok.substring(1)));
                } catch (Exception e) {
                    throw new Exception("Error @ " + t.start() + ". Invalid NAG.");
                }

                // Suffixes
            } else if (tok.matches("[?!]{1,2}")) {

                if (getLast(ravDepth).size() == 0)
                    throw new Exception("Error @ " + t.start() + ". Suffix given before a move.");

                switch (tok) {

                    case "!":
                        getLast(ravDepth).get(getLast(ravDepth).size() - 1).setNAG(1);
                        break;
                    case "?":
                        getLast(ravDepth).get(getLast(ravDepth).size() - 1).setNAG(2);
                        break;
                    case "!!":
                        getLast(ravDepth).get(getLast(ravDepth).size() - 1).setNAG(3);
                        break;
                    case "!?":
                        getLast(ravDepth).get(getLast(ravDepth).size() - 1).setNAG(4);
                        break;
                    case "?!":
                        getLast(ravDepth).get(getLast(ravDepth).size() - 1).setNAG(5);
                        break;
                    case "??":
                        getLast(ravDepth).get(getLast(ravDepth).size() - 1).setNAG(6);
                        break;
                    default:
                        throw new Exception("Error @ " + t.start() + ". Invalid suffix.");
                }

                // RAV
            } else if (tok.matches("\\(")) {

                ++ravDepth;

                if (parsedMoves.size() == 0)
                    throw new Exception("Error @ " + t.start() + ". RAV given before a move.");

                // t.region(parseRav(t.end(), parsedMoves.get(parsedMoves.size() - 1)),
                // t.regionEnd());

                // Move numbers
            } else if (tok.matches("\\)")) {
                --ravDepth;

                if (ravDepth < 0)
                    throw new Exception("Error @ " + t.start() + ". Unmatched closing parentheses.");

            } else if (tok.matches("[\\d]+")) {

                // if ((int) Math.ceil((parsedMoves.size() + 1) / 2.0) != Integer.parseInt(tok))
                // throw new Exception("Error @ " + t.start() + ". Unexpected move number.");

                // TODO: check periods to see if they match the move (... for black moves etc.)

            } else if (tok.matches("[A-Za-z0-9][A-Za-z0-9_+#=:\\-]*")) {

                if (ravDepth == 0)
                    parsedMoves.add(new PGNMove(tok));
                else {

                    ArrayList<PGNMove> a = getLast(ravDepth);
                    a.add(new PGNMove(tok));

                }

            }

        }

    }

    private ArrayList<PGNMove> getLast(int ravDepth) {

        ArrayList<PGNMove> a = parsedMoves;
        for (int i = 0; i < ravDepth; i++) {
            a = a.get(a.size() - 1).getRav();
        }

        return a;

    }

}
