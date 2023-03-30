package game;

public class Timestamp {

    private long start;
    private long end;

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public Timestamp(long start) {
        this.start = start;
    }

    public Timestamp(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public long getLength() {

        if (end <= 0)
            return -1;
        else
            return end - start;

    }

}
