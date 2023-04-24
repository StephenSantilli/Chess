package game.engine;

public abstract class UCIOption<E> {

    protected UCIEngine engine;
    protected String name;
    protected E value;
    protected E def;
    protected E min;
    protected E max;
    protected E[] vars;

    public UCIEngine getEngine() {
        return engine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public E getDef() {
        return def;
    }

    public void setDef(E def) {
        this.def = def;
    }

    public E getMin() {
        return min;
    }

    public void setMin(E min) {
        this.min = min;
    }

    public E getMax() {
        return max;
    }

    public void setMax(E max) {
        this.max = max;
    }

    public E[] getVars() {
        return vars;
    }

    public void setVars(E[] vars) {
        this.vars = vars;
    }

    abstract public void set(E value) throws Exception;

    abstract public E get() throws Exception;

}
