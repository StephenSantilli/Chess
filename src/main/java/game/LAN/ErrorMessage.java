package game.LAN;

public class ErrorMessage extends Message {

    public static final int NORMAL = 0;
    public static final int FATAL = 1;

    private final int severity;
    private final String reason;

    public int getSeverity() {
        return severity;
    }

    public String getReason() {
        return reason;
    }

    public ErrorMessage(int severity, String reason) {

        super("error", severity + "", reason);

        this.severity = severity;
        this.reason = reason;

    }

    public ErrorMessage(String msg) throws Exception {

        super(msg);

        if (args.size() != 3)
            throw new Exception("Invalid error message.");

        try {
            severity = Integer.parseInt(args.get(1));
        } catch (Exception e) {
            throw new Exception("Severity not a number.");
        }

        if (severity != NORMAL && severity != FATAL)
            throw new Exception("Invalid severity.");

        reason = args.get(2);

    }

}
