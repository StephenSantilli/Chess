package game.LAN;

import java.util.Date;

public class ChatMessage extends Message {

    private final Date timestamp;
    private final String message;

    public ChatMessage(Date timestamp, String message) {

        super("chat", timestamp.getTime() + "", message);

        this.timestamp = timestamp;
        this.message = message;

    }

    public ChatMessage(String msg) throws Exception {

        super(msg);

        if (args.size() != 3)
            throw new Exception("Invalid chat message.");

        try {
            this.timestamp = new Date(Long.parseLong(args.get(1)));
        } catch (Exception e) {
            throw new Exception("Invalid timestamp.");
        }

        this.message = args.get(2);

    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

}
