import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The radar itself. It doesn't know what a speed limit or a seat belt is -
 * it just runs every observation past the list of rules it was configured
 * with and turns whatever comes back into a fine. New rules get added from
 * the outside (see Main), this class never has to change for that.
 */
public class Radar {

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
