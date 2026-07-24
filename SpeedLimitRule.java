/**
 * Speed limit rule for one car type.
 * Truck and Private each get their own instance of this with their own limit
 * and fee - that way adding a limit for another car type (or changing an
 * existing one) is just a constructor call in Main, not a code change here.
 */
public class SpeedLimitRule implements Rule {

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
