import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // Rules the radar currently enforces. Notice Bus doesn't have a speed
        // rule yet - to add one later you'd just add another SpeedLimitRule
        // instance here, Radar itself would not need to be touched.
        List<Rule> rules = new ArrayList<>();
        rules.add(new SeatBeltRule(100));
        rules.add(new SpeedLimitRule(CarType.TRUCK, 60, 250));
        rules.add(new SpeedLimitRule(CarType.PRIVATE, 80, 300));

        Radar radar = new Radar(rules);

        // A batch of readings coming in from the physical radar.
        List<Observation> observations = new ArrayList<>();
        observations.add(new Observation("ABC1234", LocalDate.of(2026, 7, 20), CarType.PRIVATE, 94, false));
        observations.add(new Observation("XYZ777", LocalDate.of(2026, 7, 20), CarType.TRUCK, 45, true));
        observations.add(new Observation("TRK900", LocalDate.of(2026, 7, 21), CarType.TRUCK, 85, false));
        observations.add(new Observation("TRK900", LocalDate.of(2026, 7, 22), CarType.TRUCK, 90, true));
        observations.add(new Observation("CAB4567", LocalDate.of(2026, 7, 22), CarType.BUS, 65, false));

        for (Observation observation : observations) {
            Fine fine = radar.scan(observation);
            if (fine != null) {
                System.out.println(fine);
            }
        }

        System.out.println("---- Fines per plate ----");
        for (Map.Entry<String, Integer> entry : radar.getTotalsByPlate().entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue() + " EGP");
        }

        System.out.println();
        System.out.println("---- Violations count per rule ----");
        for (Map.Entry<String, Integer> entry : radar.getViolationCounts().entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

enum CarType {
    PRIVATE,
    TRUCK,
    BUS
}

/**
 * One reading captured by the physical radar.
 * This is just a data holder - the radar unit itself fills these fields in,
 * we don't care here how it got the numbers.
 */
class Observation {

    private final String plateNumber;
    private final LocalDate date;
    private final CarType carType;
    private final int speed;
    private final boolean seatBeltFastened;

    public Observation(String plateNumber, LocalDate date, CarType carType, int speed, boolean seatBeltFastened) {
        this.plateNumber = plateNumber;
        this.date = date;
        this.carType = carType;
        this.speed = speed;
        this.seatBeltFastened = seatBeltFastened;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public CarType getCarType() {
        return carType;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isSeatBeltFastened() {
        return seatBeltFastened;
    }
}

/**
 * A single traffic rule.
 *
 * The radar doesn't know anything about speed limits or seat belts - it just
 * asks every rule it was given "does this observation break you?". To add a
 * new rule to the system you write a class that implements this interface
 * and register it, nothing else needs to change.
 */
interface Rule {

    /**
     * Name used when reporting how many times each rule got broken.
     */
    String getName();

    /**
     * Checks the observation against this rule.
     * Returns the violation if the rule was broken, or null if everything is fine.
     */
    Violation evaluate(Observation observation);
}

class SeatBeltRule implements Rule {

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
        return new Violation("Seatbelt not fastened", fee);
    }
}

/**
 * Speed limit rule for one car type.
 * Truck and Private each get their own instance of this with their own limit
 * and fee - that way adding a limit for another car type (or changing an
 * existing one) is just a constructor call in Main, not a code change here.
 */
class SpeedLimitRule implements Rule {

    private final CarType carType;
    private final int maxSpeed;
    private final int fee;

    public SpeedLimitRule(CarType carType, int maxSpeed, int fee) {
        this.carType = carType;
        this.maxSpeed = maxSpeed;
        this.fee = fee;
    }

    @Override
    public String getName() {
        return "Speed limit - " + carType;
    }

    @Override
    public Violation evaluate(Observation observation) {
        if (observation.getCarType() != carType) {
            return null;
        }
        if (observation.getSpeed() <= maxSpeed) {
            return null;
        }
        String reason = "speed of " + observation.getSpeed() + " exceeded max allowed " + maxSpeed;
        return new Violation(reason, fee);
    }
}

class Violation {

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

class Fine {

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

/**
 * The radar itself. It doesn't know what a speed limit or a seat belt is -
 * it just runs every observation past the list of rules it was configured
 * with and turns whatever comes back into a fine. New rules get added from
 * the outside (see Main), this class never has to change for that.
 */
class Radar {

    private final List<Rule> rules;
    private final List<Fine> fines = new ArrayList<>();
    private final Map<String, Integer> violationCounts = new LinkedHashMap<>();

    public Radar(List<Rule> rules) {
        this.rules = rules;
    }

    /**
     * Feeds one observation to the radar. Returns the fine if any rule was
     * broken, or null if the car was fully compliant.
     */
    public Fine scan(Observation observation) {
        List<Violation> violations = new ArrayList<>();

        for (Rule rule : rules) {
            Violation violation = rule.evaluate(observation);
            if (violation != null) {
                violations.add(violation);
                violationCounts.merge(rule.getName(), 1, Integer::sum);
            }
        }

        if (violations.isEmpty()) {
            return null;
        }

        Fine fine = new Fine(observation.getPlateNumber(), observation.getDate(), violations);
        fines.add(fine);
        return fine;
    }

    /**
     * Total fine amount per plate number, across every observation scanned so far.
     */
    public Map<String, Integer> getTotalsByPlate() {
        Map<String, Integer> totals = new LinkedHashMap<>();
        for (Fine fine : fines) {
            totals.merge(fine.getPlateNumber(), fine.getTotalAmount(), Integer::sum);
        }
        return totals;
    }

    /**
     * How many times each rule has been broken so far.
     */
    public Map<String, Integer> getViolationCounts() {
        return violationCounts;
    }

    public List<Fine> getAllFines() {
        return fines;
    }
}
