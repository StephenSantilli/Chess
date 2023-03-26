package game;

public class Chat {

    public static final int MAX_LENGTH = 250;

    private Player player;
    private long timestamp;
    private String message;

    private boolean systemMessage;

    public Player getPlayer() {
        return player;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSystemMessage() {
        return systemMessage;
    }

    public Chat(Player player, long timestamp, String message) {
        this.player = player;
        this.timestamp = timestamp;
        this.message = message;
    }

    public Chat(Player player, long timestamp, String message, boolean systemMessage) {
        this.player = player;
        this.timestamp = timestamp;
        this.message = message;
        this.systemMessage = systemMessage;
    }

}
