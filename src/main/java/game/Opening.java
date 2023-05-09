package game;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import game.PGN.PGNParser;
import game.Player.Type;

/**
 * Openings database from https://github.com/lichess-org/chess-openings
 */
public class Opening {

    // public static void main(String[] args) {

    //     try (Scanner s = new Scanner(new File("/Users/stephen/Documents/Chess/bin/main/csv/eco.tsv"))) {

    //         String updated = "";

    //         while (s.hasNextLine()) {

    //             String line = s.nextLine();
    //             String[] sp = line.split("\t");

    //             try {
    //                 Game game = new Game(new PGNParser(sp[2]), new GameSettings(0, 0, false,
    //                         false, false, false),
    //                         false);

    //                 String fen = game.getLastPos().toString();
    //                 updated += line + "\t" + fen.substring(0, fen.indexOf(" ")) + "\n";
    //             } catch (Exception e) {
    //                 System.out.println(e);
    //                 System.out.println(line);
    //             }

    //         }

    //         try (FileWriter w = new FileWriter("/Users/stephen/Documents/Chess/src/main/resources/csv/updated.tsv")) {

    //             w.write(updated);
    //             w.flush();

    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    // }

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