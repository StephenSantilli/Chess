package game.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class UCIEngine {

    public static void main(String[] args) throws IOException {

        UCIEngine e = new UCIEngine(new File("./Stockfish"));
        e.setOption("Skill Level", "1");
        e.waitReady();
        e.setPosition("startpos", "e2e4");
        // System.out.println(e.getBestMove(10));

    }

    private String name;
    private String author;

    protected BufferedReader input;
    protected BufferedWriter output;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    private void println(String command) throws IOException {
        System.out.println("Outputting: " + command);
        output.write(command + "\n");
        output.flush();
    }

    public void waitReady() throws IOException {

        println("isready");

        String rec = input.readLine();
        while (!rec.equals("readyok")) {
            System.out.println(rec);
            rec = input.readLine();
        }
        System.out.println(rec);

    }

    public void setPosition(String fen, String... moves) throws IOException {

        println("position " + fen + " moves " + String.join(" ", moves));

    }

    public String getBestMove(int depth, long wtime, long btime, long winc, long binc) throws IOException {

        String d = " depth " + depth;
        if (depth <= 0)
            d = "";

        String wt = " wtime " + wtime;
        if (wtime <= 0)
            wt = "";

        String bt = " btime " + btime;
        if (btime <= 0)
            bt = "";

        String wi = " winc " + winc;
        if (winc <= 0)
            wi = "";

        String bi = " binc " + binc;
        if (binc <= 0)
            bi = "";

        println("go" + d + wt + bt + wi + bi);

        String rec = input.readLine();
        while (!rec.startsWith("bestmove ")) {
            System.out.println(rec);
            rec = input.readLine();
        }

        System.out.println(rec);
        String[] sp = rec.split(" ");
        return sp[1];

    }

    public void setOption(String name, String value) throws IOException {

        println("setoption name " + name + " value " + value);

    }

    public UCIEngine(File enginePath) throws IOException {

        ProcessBuilder pb = null;

        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            pb = new ProcessBuilder("cmd", "/c", "stockfish");
        } else {
            pb = new ProcessBuilder("stockfish");
        }

        pb.directory(enginePath);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        input = p.inputReader();
        output = p.outputWriter();

        String rec = input.readLine();
        output.write("uci\n");
        output.flush();

        while (!rec.equals("uciok")) {
            System.out.println(rec);

            if (rec.startsWith("id name ")) {
                this.name = rec.substring(8);
            } else if (rec.startsWith("id author ")) {
                this.author = rec.substring(10);
            }

            rec = input.readLine();
        }

        println("ucinewgame");
        waitReady();

    }

}
