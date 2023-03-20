package game.LAN;

public class Message {
    
    private String text;
    
    private String[] args;

    public String getText() {
        return text;
    }

    public String[] getArgs() {
        return args;
    }

    public Message(String text) {

        this.text = text;

        args = text.split("(?<!\\\\);");

        for(int i = 0; i < args.length; i++) {
            args[i] = args[i].replaceAll("\\\\;", ";");
        }

    }

    public Message(String... args) {

        this.args = args;
        this.text = "";

        for (int i = 0; i < args.length; i++) {
            text += args[i].replaceAll(";", "\\;") + ";";
        }
        
    }

    @Override
    public String toString() {

        return text;

    }

}
