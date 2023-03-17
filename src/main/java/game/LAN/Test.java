package game.LAN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) throws Exception {

        Socket s = new Socket("localhost", 49265);
        System.out.println("connectin");
        PrintWriter writer = new PrintWriter((s.getOutputStream()), true);

        new Thread(() -> {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String line = input.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = input.readLine();
                }
            } catch (Exception e) {

            }
        }).start();

        Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();
        while (line != null) {

            writer.println(line);
            line = scan.nextLine();

        }

        scan.close();

    }

}
