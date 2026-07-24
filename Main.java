import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // Rules the radar currently enforces. Notice Bus doesn't have a speed
        // rule yet - to add one later you'd just add another SpeedLimitRule
        // instance here, Radar.java itself would not need to be touched.
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
