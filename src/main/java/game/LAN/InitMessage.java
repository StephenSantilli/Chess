package game.LAN;

import game.Player;

public class InitMessage extends Message {

    private final String version;

    private final String name;

    public InitMessage(String version, String name) {

        super("init", version, name);

        this.version = version;
        this.name = name;

    }

    public InitMessage(String msg) throws Exception {

        super(msg);

        if (args.size() != 3)
            throw new Exception("Invalid init message.");

        this.version = args.get(1);
        this.name = args.get(2);

        if (!name.matches(Player.NAME_REGEX))
            throw new Exception("Invalid name.");

    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

}
