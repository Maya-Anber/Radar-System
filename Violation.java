public class Violation {

    private final String reason;
    private final int fee;

    public Violation(String reason, int fee) {
        this.reason = reason;
        this.fee = fee;
    }

    public String getReason() {
        return reason;
    }

    public int getFee() {
        return fee;
    }
}
