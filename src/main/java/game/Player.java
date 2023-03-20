package game;

public class Player {

    public static final int MAX_NAME_LENGTH = 20;

    public static final String NAME_REGEX = "[A-z0-9!@#$%^&*()_\\-\\+=\"',. ?:\\/\\[\\]\\{\\}]{1," + MAX_NAME_LENGTH
            + "}";

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player(String name) {

        this.name = name;

    }

}
