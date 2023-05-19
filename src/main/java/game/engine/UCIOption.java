package game.engine;

/**
 * A generic option for a UCI engine.
 */
public abstract class UCIOption<E> {

    /**
     * The engine associated with this option.
     */
    protected UCIEngine engine;

    /**
     * The name of the option.
     */
    protected String name;

    /**
     * The current value of the option.
     */
    protected E value;

    /**
     * The default value of the option.
     */
    protected E def;

    //TODO: Why aren't these in their respective class?
    /**
     * The min value of this option.
     */
    protected E min;

    /**
     * The max value of this option.
     */
    protected E max;

    /**
     * The variables associated with this option.
     */
    protected E[] vars;

    /**
     * Gets the engine.
     * 
     * @return {@link #engine}
     */
    public UCIEngine getEngine() {
        return engine;
    }

    /**
     * Gets the name of the option.
     * 
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this option.
     * 
     * @param name The name.
     * @see #name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value.
     * 
     * @return {@link #value}
     */
    public E getValue() {
        return value;
    }

    /**
     * Sets the value.
     * 
     * @param value The value.
     * @see #value
     */
    public void setValue(E value) {
        this.value = value;
    }

    /**
     * Gets the default.
     * 
     * @return {@link #def}
     */
    public E getDef() {
        return def;
    }

    /**
     * Sets the default.
     * 
     * @param def The default.
     * @see #def
     */
    public void setDef(E def) {
        this.def = def;
    }

    /**
     * Gets the minimum value.
     * 
     * @return {@link #min}
     */
    public E getMin() {
        return min;
    }

    /**
     * Sets the minimum value.
     * 
     * @param min The minimum value.
     * @see #min
     */
    public void setMin(E min) {
        this.min = min;
    }

    /**
     * Gets the maximum value.
     * 
     * @return {@link #max}
     */
    public E getMax() {
        return max;
    }

    /**
     * Sets the maximum value.
     * 
     * @param max The max value.
     * @see #max
     */
    public void setMax(E max) {
        this.max = max;
    }

    /**
     * Gets the predefined variables.
     * 
     * @return {@link #vars}
     */
    public E[] getVars() {
        return vars;
    }

    /**
     * Sets the variables.
     * 
     * @param vars A list of the variables.
     * @see #vars
     */
    public void setVars(E[] vars) {
        this.vars = vars;
    }

    /**
     * Sets the value of the option.
     * 
     * @param value The value to set the option to.
     * @throws Exception If there is an error setting the option with the engine.
     */
    abstract public void set(E value) throws Exception;

    /**
     * Gets the value of the option.
     * 
     * @return The value of the option.
     * @throws Exception If there is an error getting the value from the engine.
     */
    abstract public E get() throws Exception;

}
