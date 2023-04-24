package game.engine;

import java.io.IOException;

public class UCIButton extends UCIOption<Void> {

    public UCIButton(UCIEngine engine, String name) {
        this.engine = engine;
        this.name = name;
    }

    public void set(Void value) throws IOException {
        engine.setOption(name, "");
        this.value = value;

    }

    public Void get() throws IOException {
        return value;
    }

}
