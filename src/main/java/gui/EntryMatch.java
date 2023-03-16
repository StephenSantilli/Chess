package gui;

public class EntryMatch {

    private boolean start;
    private int index;

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public EntryMatch(boolean start, int index) {
        this.start = start;
        this.index = index;
    }

}
