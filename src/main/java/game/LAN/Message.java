package game.LAN;

public class Message {

    private String name, moveText;

    public String getName() {
        return name;
    }

    public String getMoveText() {
        return moveText;
    }

    public Message(String name, String moveText) {

        this.name = name;
        this.moveText = moveText;

    }

    public Message(String message) throws Exception {

        if (!message.startsWith("Chess;"))
            throw new Exception("Invalid message.");

        String[] el = message.split(";");

        if (el.length <= 1)
            throw new Exception("No message body.");

        if(el.length >= 2) {
            name = el[1];
        }

        if(el.length >= 3) {
            moveText = el[2];
        }

    }

    public String toString() {

        String str = "Chess;";

        if (name != null) {
            str += name;
        }
        str += ";";

        if (moveText != null) {
            str += moveText;
        }
        str += ";";

        return str;

    }

}
