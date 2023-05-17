package game.LAN;

import java.util.ArrayList;

public class Message {

    public static final Message DRAW_OFFER = new Message("draw");
    public static final Message DRAW_ACCEPT = new Message("drawaccept");
    public static final Message DRAW_DECLINE = new Message("drawdecline");
    public static final Message RESIGN = new Message("resign");
    public static final Message START = new Message("start");
    public static final Message STARTED = new Message("started");

    protected ArrayList<String> args;

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

    public ArrayList<String> getArgs() {
        return args;
    }

    @Override
    public boolean equals(Object compare) {

        if (!(compare instanceof Message))
            return false;

        Message casted = (Message) (compare);

        boolean same = args.size() == casted.getArgs().size();

        for (int i = 0; same && i < args.size(); i++) {

            if (!args.get(i).equals(casted.getArgs().get(i)))
                same = false;

        }

        return same;
      
    }

    @Override
    public String toString() {

        String text = "";

        for (int i = 0; i < args.size(); i++) {
            text += args.get(i).replaceAll(";", "\\\\;") + ";";
        }

        return text;

    }

}
