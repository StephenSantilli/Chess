package game.LAN;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Sends the challenge to the {@link ChallengeSearcher} that requested it.
 */
public class ChallengeSender implements Runnable {

    private Challenge challenge;
    private DatagramSocket sock;

    public ChallengeSender(Challenge challenge, DatagramSocket sock) {

        this.challenge = challenge;
        this.sock = sock;

    }

    public void run() {
        try {

            DatagramPacket packet = new DatagramPacket((challenge.toString()).getBytes(), challenge.toString().length(),
                    challenge.getAddress(), Client.PORT);

            sock.send(packet);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
