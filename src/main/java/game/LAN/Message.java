package game.LAN;

import java.util.ArrayList;

public class Message {

    private ArrayList<String> args;

    public ArrayList<String> getArgs() {
        return args;
    }

    public Message(String text) {

        this.args = new ArrayList<String>();

        String[] split = text.split("(?<!\\\\);");

        for (int i = 0; i < split.length; i++) {
            args.add(split[i].replaceAll("\\\\;", ";"));
        }

    }

    public Message(String... split) {

        this.args = new ArrayList<String>();

        for (int i = 0; i < split.length; i++) {
            args.add(split[i].replaceAll("\\\\;", ";"));
        }

    }

    @Override
    public String toString() {

        String text = "";

        for (int i = 0; i < args.size(); i++) {
            text += args.get(i).replaceAll(";", "\\;") + ";";
        }

        return text;

    }

}
