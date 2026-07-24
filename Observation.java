import java.time.LocalDate;

/**
 * One reading captured by the physical radar.
 * This is just a data holder - the radar unit itself fills these fields in,
 * we don't care here how it got the numbers.
 */
public class Observation {

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
