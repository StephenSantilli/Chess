package game.PGN;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import game.Game;
import game.Position;

/**
 * Class to parse a .pgn file following this specification:
 * http://www.saremba.de/chessgml/standards/pgn/pgn-complete.htm
 */
public class PGNParser {

    /** The content of the PGN file. */
    private String text;

    /** A list of all of the {@code String} moves in the PGN text. */
    private ArrayList<String> moves;

    /** The parsed moves. */
    private ArrayList<PGNMove> parsedMoves;

    /** The name of the tournament or match event. */
    private String event;

    /**
     * The location of the event. {@code [City], [Region] [IOC country code]}.
     * <br>
     * <br>
     * Ex: {@code New York City, NY USA}.
     */
    private String site;

    /**
     * The starting date of the event. {@code YYYY.MM.DD} format. {@code ??} used
     * when unknown.
     */
    private String date;

    /** The round of the game in the event. */
    private String round;

    /** The player of the white pieces. {@code Lastname, Firstname}. */
    private String white;

    /** The player of the white pieces. {@code Lastname, Firstname}. */
    private String black;

    /**
     * The result of the game. {@code [White score]-[Black score]} or {@code *}
     * (when game has not concluded.)
     * <br>
     * <br>
     * <b>Potential values:</b>
     * <ul>
     * <li>{@code 1-0}
     * <li>{@code 0-1}
     * <li>{@code 1/2-1/2}
     * <li>{@code *}
     */
    private String result;

    // /** The person annotating the game. */
    // private String annotator;

    // /** The number of half-moves played. */
    // private String plyCount;

    // /**
    // * The time control of the game.
    // * {@code [moves]/[seconds]:[sudden death seconds]} or
    // * {@code [seconds]+[additional seconds per move]}
    // */
    // private String timeControl;

    // /**
    // * The time the game was started at, in {@code HH:MM:SS} format. In local
    // clock
    // * time.
    // */
    // private String time;

    // /** {@link #time}, but in UTC. */
    // private String utcTime;

    // /** {@link #date}, but in UTC. */
    // private String utcDate;

    // /**
    // * The way the game was terminated.
    // * <br>
    // * <br>
    // * <b>Potential values:</b>
    // * <ul>
    // * <li>abandoned
    // * <li>adjudication
    // * <li>death
    // * <li>emergency
    // * <li>normal
    // * <li>rules infraction
    // * <li>time forfeit
    // * <li>unterminated
    // */
    // private String termination;

    // /**
    // * The type of play.
    // * <br>
    // * <br>
    // * <b>Potential values:</b>
    // * <ul>
    // * <li>OTB (over-the-board)
    // * <li>ICS (internet chess server)
    // * <li>PM (paper mail)
    // * <li>EM (email)
    // * <li>TC (general telecommunication)
    // */
    // private String mode;

    // /**
    // * 0 if default initial board position used. {@code 1} if custom board
    // position
    // * used. Should be defined in {@link #FEN}.
    // */
    // private String setup;

    // /**
    // * The initial position of the board, in FEN notation. {@link #setup} must be
    // * {@code 1}.
    // */
    // private String FEN;

    // /** The FIDE title of the white player. */
    // private String whiteTitle;

    // /** The FIDE title of the white player. */
    // private String blackTitle;

    // /** The USCF title of the white player. */
    // private String whiteUSCF;

    // /** The USCF title of the black player. */
    // private String blackUSCF;

    // /**
    // * The network address or email of the white player. '-' used for players
    // * without an email.
    // */
    // private String whiteNA;

    // /**
    // * The network address or email of the black player. '-' used for players
    // * without an
    // * email.
    // */
    // private String blackNA;

    // /** Whether or not the white player is {@code human} or {@code program}. */
    // private String whiteType;

    // /** Whether or not the black player is {@code human} or {@code program}. */
    // private String blackType;

    // /** The elo of the white player. */
    // private String whiteElo;

    // /** The elo of the black player. */
    // private String blackElo;

    // /**
    // * A date value, formatted like {@link #date}, that is the starting date of
    // the
    // * event.
    // */
    // private String eventDate;

    // /** The sponsor of the event. */
    // private String eventSponsor;

    // /** The section of the event. {@code Open} or {@code Reserve}. */
    // private String section;

    // /** The stage of the event, e.g., {@code Preliminary} or {@code Semifinal}.
    // */
    // private String stage;

    // /** The board number in a team event, and also in a simultaneous exhibition.
    // */
    // private String board;

    /** Used for any extra, non-standard tags. */
    private ArrayList<String> extraTags;

    public static void main(String[] args) throws Exception {

        Scanner s = new Scanner(new FileReader("./test pgns/chesscom with clock.pgn"));
        String str = "";
        while (s.hasNextLine()) {
            str += s.nextLine().trim() + "\n";
        }
        PGNParser p = new PGNParser(str);

        System.out.println(p.outputPGN());

    }

    public PGNParser(String text) throws Exception {

        // this.text = String.join(" ", text.trim().split("\n"));
        this.text = text.trim();
        extraTags = new ArrayList<String>();
        moves = new ArrayList<String>();
        parsedMoves = new ArrayList<PGNMove>();

        parseTags();
        parseMoves();

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
        extraTags = new ArrayList<String>();
        moves = new ArrayList<String>();
        parsedMoves = new ArrayList<PGNMove>();

        // TODO: ADD SUPPORT FOR TAGS

        for (int i = 1; i < game.getPositions().size(); i++) {

            Position p = game.getPositions().get(i);

            String comment = null;

            if (includeClock && game.getSettings().getTimePerSide() > 0 && game.getPositions().get(i - 1).getTimerEnd() > 0) {
                comment = "{[%clk " + millisToOutputFormat(game.getPositions().get(i - 1).getTimerEnd()) + "]}";
            }

            parsedMoves.add(new PGNMove(p.getMoveString(), comment, null, null, null));

        }

    }

    public String outputPGN() {
        return outputPGN(true);
    }

    public String outputPGN(boolean includeTags) {

        String str = "";

        if (includeTags) {
            str += "[Event \"" + event + "\"]\n";
            str += "[Site \"" + site + "\"]\n";
            str += "[Date \"" + date + "\"]\n";
            str += "[Round \"" + round + "\"]\n";
            str += "[White \"" + white + "\"]\n";
            str += "[Black \"" + black + "\"]\n";
            str += "[Result \"" + result + "\"]\n";

            for (int i = 0; i + 1 < extraTags.size(); i++) {

                str += "[" + extraTags.get(i) + " \"" + extraTags.get(++i) + "\"]\n";

            }

            str += "\n";
        }

        String moveList = "";
        for (int i = 0; i < parsedMoves.size(); i++) {

            moveList += ((i / 2) + 1) + ". " + parsedMoves.get(i) + " ";

            if (i + 1 < parsedMoves.size()) {

                if (!parsedMoves.get(i).getCommentary().equals("") || parsedMoves.get(i).getNAG() != 0) {
                    moveList += ((i / 2) + 1) + "... ";
                }

                moveList += parsedMoves.get(++i) + " ";
            }

        }

        while (moveList.length() > 80) {
            int find = moveList.lastIndexOf(" ", 80);
            if (find <= -1) {
                find = 79;
            }
            str += moveList.substring(0, find + 1).trim() + "\n";
            moveList = moveList.substring(find + 1);
        }
        str += moveList;
        str += "\n";

        return str;
    }

    private static final String MOVE_REGEX = "(?<num>(\\d+(\\.\\.\\.)?)|\\d+\\.?)?\\s*(?<move>(((([QKRBNP][a-h]?[1-8]?)?([a-h][1-8])(=[QRBN])?)[+#]?)|(([QKRBNP]?[a-h]?[1-8]?)?(x[a-h][1-8])(=[QRBN])?[+#]?)|(O-O)|(O-O-O)))(?<suffix>[?!]{0,2})(((?<comm>\\s*\\{[^\\}]+\\}))|((?<NAG>\\s*\\$\\d{1,3}))|((?<res>\\s*((1\\-0)|(0\\-1)|(\\*)|(1\\/2\\-1\\/2))))){0,3}\\s*(?<eol>\\;[^\n]*)?";

    private void parseMoves() throws Exception {

        Matcher m = Pattern.compile(MOVE_REGEX).matcher(text);

        while (m.find()) {

            String comment = m.group("comm");

            parsedMoves.add(new PGNMove(m.group("move"), comment, m.group("NAG"), m.group("res"), m.group("suffix")));

        }

    }

    private static final String TAG_REGEX = "\\[([A-z0-9_]+) ?\"([^\"\n]*)\"\\]";

    private void parseTags() throws Exception {

        Matcher m = Pattern.compile(TAG_REGEX).matcher(text);

        ArrayList<String> tags = new ArrayList<String>();

        while (m.find()) {

            tags.add(m.group(1));
            tags.add(m.group(2));

        }

        for (int i = 0; i + 1 < tags.size(); i++) {

            String key = tags.get(i);
            String value = tags.get(++i);

            switch (key) {

                case "Event":
                    event = value;
                    break;
                case "Site":
                    site = value;
                    break;
                case "Date":
                    date = value;
                    break;
                case "Round":
                    round = value;
                    break;
                case "White":
                    white = value;
                    break;
                case "Black":
                    black = value;
                    break;
                case "Result":
                    result = value;
                    break;
                default:
                    extraTags.add(key);
                    extraTags.add(value);
                    break;
            }
        }

        /*
         * if (event == null || site == null || date == null || round == null || white
         * == null || black == null
         * || result == null)
         * throw new Exception(
         * "Part of seven tag roster is missing. You must include at least Event, Site, Date, Round, White, Black, and Result."
         * );
         */

        // TODO: add checking for value validity

    }

    public String getText() {
        return text;
    }

    public ArrayList<String> getMoves() {
        return moves;
    }

    public String getEvent() {
        return event;
    }

    public String getSite() {
        return site;
    }

    public String getDate() {
        return date;
    }

    public String getRound() {
        return round;
    }

    public String getWhite() {
        return white;
    }

    public String getBlack() {
        return black;
    }

    public String getResult() {
        return result;
    }

    public ArrayList<PGNMove> getParsedMoves() {
        return parsedMoves;
    }

}
