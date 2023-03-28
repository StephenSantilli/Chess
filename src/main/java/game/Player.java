package game;

public class Player {

    public static final int MAX_NAME_LENGTH = 20;

    public static final String NAME_REGEX = "[A-Za-z0-9!@#$%^&*()_\\-\\+=\"',. ?:\\/\\[\\]\\{\\}]{1," + MAX_NAME_LENGTH
            + "}";

    private String name;
    private boolean white;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Player))
            return false;

        Player casted = (Player) o;

        return name.equals(casted.getName()) && white == casted.isWhite();

    }

    public Player(String name) {

        this.name = name;

    }

    public Player(String name, boolean white) {

        this.name = name;
        this.white = white;

    }

}
