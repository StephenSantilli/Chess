package game.engine;

import java.io.IOException;

public class UCICombo extends UCIOption<String> {

    public UCICombo(UCIEngine engine, String name, String def, String value, String... vars) {
        this.engine = engine;
        this.name = name;
        this.def = def;
        this.value = value;
        this.vars = vars;

    }

    public void set(String value) throws IOException {
        engine.setOption(name, value);
        this.value = value;

    }

    public String get() throws IOException {
        return value;
    }

}
