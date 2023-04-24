package game.engine;

import java.io.IOException;

public class UCICheck extends UCIOption<Boolean> {

    public UCICheck(UCIEngine engine, String name, boolean def, boolean value) {
        this.engine = engine;
        this.name = name;
        this.def = def;
        this.value = value;
    }

    public void set(Boolean value) throws IOException {
        engine.setOption(name, value ? "true" : "false");
        this.value = value;

    }

    public Boolean get() throws IOException {
        return value;
    }

}
