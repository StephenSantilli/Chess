package game;

import java.io.File;
import java.util.*;

/**
 * Openings database from https://github.com/lichess-org/chess-openings
 */
public class Opening {

    private final String code;
    private final String name;
    private final String sequence;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSequence() {
        return sequence;
    }

    public Opening(String code, String name, String sequence) {
        this.code = code;
        this.name = name;
        this.sequence = sequence;
    }

    /**
     * Gets the opening of the position with the given FEN from the openings in the
     * given file.
     * 
     * @param fen      The FEN of the position to check.
     * @param openings A tab-separated values file, formatted
     *                 {@code ECO code]\t[opening name]\t[opening sequence of
     *                 moves]\t[opening FEN]}
     * @return The opening that matches the position.
     */
    public static Opening getOpening(String fen, File openings) {

        Opening found = null;
        try (Scanner s = new Scanner(openings)) {

            while (s.hasNextLine() && found == null) {

                String line = s.nextLine().trim();
                String[] a = line.split("\t");

                if (fen.startsWith(a[3])) {

                    found = new Opening(a[0], a[1], a[2]);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return found;

    }

}