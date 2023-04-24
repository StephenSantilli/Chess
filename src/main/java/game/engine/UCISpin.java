package game.engine;

import java.io.IOException;

public class UCISpin extends UCIOption<Integer> {

    public UCISpin(UCIEngine engine, String name, int def, int value, int min, int max) {
        this.engine = engine;
        this.name = name;
        this.def = def;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public void set(Integer value) throws IOException {
        engine.setOption(name, value + "");
        this.value = value;
    }

    public Integer get() throws IOException {
        return value;
    }

}
