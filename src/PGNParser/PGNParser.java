package PGNParser;

import java.util.ArrayList;

/**
 * Class to parse a .pgn file following this specification:
 * http://www.saremba.de/chessgml/standards/pgn/pgn-complete.htm
 */
public class PGNParser {

    /** The content of the PGN file. */
    private String text;

    /** A list of all of the {@code String} moves in the PGN text. */
    private ArrayList<String> moves;

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

    /** The person annotating the game. */
    private String annotator;

    /** The number of half-moves played. */
    private String plyCount;

    /**
     * The time control of the game.
     * {@code [moves]/[seconds]:[sudden death seconds]} or
     * {@code [seconds]+[additional seconds per move]}
     */
    private String timeControl;

    /**
     * The time the game was started at, in {@code HH:MM:SS} format. In local clock
     * time.
     */
    private String time;

    private String utcTime;

    private String utcDate;

    /**
     * The way the game was terminated.
     * <br>
     * <br>
     * <b>Potential values:</b>
     * <ul>
     * <li>abandoned
     * <li>adjudication
     * <li>death
     * <li>emergency
     * <li>normal
     * <li>rules infraction
     * <li>time forfeit
     * <li>unterminated
     */
    private String termination;

    /**
     * The type of play.
     * <br>
     * <br>
     * <b>Potential values:</b>
     * <ul>
     * <li>OTB (over-the-board)
     * <li>ICS (internet chess server)
     * <li>PM (paper mail)
     * <li>EM (email)
     * <li>TC (general telecommunication)
     */
    private String mode;

    /** The initial position of the board, in FEN notation. */
    private String FEN;

    public PGNParser(String text) throws Exception {

        this.text = text;
        parseTags();

    }

    private void parseTags() throws Exception {

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

    public String getAnnotator() {
        return annotator;
    }

    public String getPlyCount() {
        return plyCount;
    }

    public String getTimeControl() {
        return timeControl;
    }

    public String getTime() {
        return time;
    }

    public String getTermination() {
        return termination;
    }

    public String getMode() {
        return mode;
    }

    public String getFEN() {
        return FEN;
    }

}
