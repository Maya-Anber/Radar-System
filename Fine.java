import java.time.LocalDate;
import java.util.List;

public class Fine {

    private final String plateNumber;
    private final LocalDate date;
    private final List<Violation> violations;

    public Fine(String plateNumber, LocalDate date, List<Violation> violations) {
        this.plateNumber = plateNumber;
        this.date = date;
        this.violations = violations;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public int getTotalAmount() {
        int total = 0;
        for (Violation violation : violations) {
            total += violation.getFee();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Traffic fine for car ").append(plateNumber).append(System.lineSeparator());
        sb.append("Total amount: ").append(getTotalAmount()).append(" EGP").append(System.lineSeparator());
        sb.append("Violations:").append(System.lineSeparator());
        for (Violation violation : violations) {
            sb.append(violation.getReason()).append(" : ").append(violation.getFee()).append(" EGP")
              .append(System.lineSeparator());
        }
        return sb.toString();
    }
}
