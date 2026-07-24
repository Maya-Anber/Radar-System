public class SeatBeltRule implements Rule {

    private final int fee;

    public SeatBeltRule(int fee) {
        this.fee = fee;
    }

    @Override
    public String getName() {
        return "Seatbelt";
    }

    @Override
    public Violation evaluate(Observation observation) {
        if (observation.isSeatBeltFastened()) {
            return null;
        }
        return new Violation("Seatbelt not fastned", fee);
    }
}
