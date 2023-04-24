package game.engine;

import java.io.IOException;

public class UCIString extends UCIOption<String> {

    public UCIString(UCIEngine engine, String name, String def, String value) {
        this.engine = engine;
        this.name = name;
        this.def = def;
        this.value = value;
    }

    public void set(String value) throws IOException {
        engine.setOption(name, value);
        this.value = value;

    }

    public String get() throws IOException {
        return value;
    }

}
