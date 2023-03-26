package game;

public class Chat {

    public static final int MAX_LENGTH = 250;

    private Player player;
    private long timestamp;
    private String message;

    public Player getPlayer() {
        return player;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public Chat(Player player, long timestamp, String message) {
        this.player = player;
        this.timestamp = timestamp;
        this.message = message;
    }

}
