package game.engine;

import java.io.IOException;

/**
 * A UCI option that can be a range of numbers, like a spinner.
 * 
 * @see UCIOption
 */
public class UCISpin extends UCIOption<Integer> {

    /**
     * Creates a new UCI spinner option.
     * 
     * @param engine The engine.
     * @param name   The name of the option.
     * @param def    The default value of the spinner.
     * @param value  The current value of the spinner.
     * @param min    The minimum value of the spinner.
     * @param max    The max value of the spinner.
     */
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
