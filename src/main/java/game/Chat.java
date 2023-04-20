package game;

/**
 * A chat message sent during the game.
 */
public class Chat {

    // TODO enforce chat limit
    /** The maximum length of a chat message. */
    public static final int MAX_LENGTH = 250;

    /** The player who created this message. */
    private final Player player;

    /** When the message was sent. */
    private final long timestamp;

    /** The content of the message, limited to {@link #MAX_LENGTH}. */
    private final String message;

    /** Whether or not the message is a system message. */
    private final boolean systemMessage;

    /** If the message is an error. */
    private final boolean error;

    /**
     * @return {@link #player}
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return {@link #timestamp}
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return {@link #message}
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return {@link #systemMessage}
     */
    public boolean isSystemMessage() {
        return systemMessage;
    }

    /**
     * @return {@link #error}
     */
    public boolean isError() {
        return error;
    }

    /**
     * Creates a new chat message.
     * 
     * @param player    The player who created the chat message.
     * @param timestamp The timestamp of when the chat message was sent.
     * @param message   The message body.
     */
    public Chat(Player player, long timestamp, String message) {

        this.player = player;
        this.timestamp = timestamp;
        this.message = message;
        this.systemMessage = false;
        this.error = false;

    }

    /**
     * Creates a new chat message.
     * 
     * @param player        The player who created the chat message.
     * @param timestamp     The timestamp of when the chat message was sent.
     * @param message       The message body.
     * @param systemMessage If the message is a systemMessage.
     */
    public Chat(Player player, long timestamp, String message, boolean systemMessage) {

        this.player = player;
        this.timestamp = timestamp;
        this.message = message;
        this.systemMessage = systemMessage;
        this.error = false;

    }

    /**
     * Creates a new chat message.
     * 
     * @param player        The player who created the chat message.
     * @param timestamp     The timestamp of when the chat message was sent.
     * @param message       The message body.
     * @param systemMessage If the message is a systemMessage.
     * @param error         If the message is an error message.
     */
    public Chat(Player player, long timestamp, String message, boolean systemMessage, boolean error) {

        this.player = player;
        this.timestamp = timestamp;
        this.message = message;
        this.systemMessage = systemMessage;
        this.error = error;

    }

}
