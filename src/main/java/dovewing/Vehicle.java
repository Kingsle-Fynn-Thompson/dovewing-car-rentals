package dovewing;

import java.util.Objects;
import java.util.Optional;

/**
 * A vehicle in the fleet. Its status can only change through markRented and
 * markReturned, which refuse any change that would make the status invalid.
 */
public final class Vehicle {

    private final String model;
    private final String reg;
    private final Money dailyRate;
    private final Money deposit;
    private final VehicleClass vehicleClass;
    private VehicleStatus status = new VehicleStatus.Available();

    public Vehicle(String model, String reg, Money dailyRate, Money deposit,
                   VehicleClass vehicleClass) {
        this.model = requireText(model, "model");
        this.reg = requireText(reg, "reg");
        this.dailyRate = Objects.requireNonNull(dailyRate, "dailyRate");
        this.deposit = Objects.requireNonNull(deposit, "deposit");
        this.vehicleClass = Objects.requireNonNull(vehicleClass, "vehicleClass");
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field);
        if (value.isBlank()) {
            throw new IllegalArgumentException("A vehicle needs a " + field);
        }
        return value;
    }

    public String model() { return model; }
    public String reg() { return reg; }
    public VehicleClass vehicleClass() { return vehicleClass; }
    public VehicleStatus status() { return status; }

    public boolean isAvailable() {
        return status instanceof VehicleStatus.Available;
    }

    /** The rental holding this vehicle, or empty if it is available. */
    public Optional<Rental> currentRental() {
        return status instanceof VehicleStatus.Rented rented
                ? Optional.of(rented.rental())
                : Optional.empty();
    }

    /** What renting this vehicle for a number of days costs. */
    public Money costFor(int days) {
        return vehicleClass.cost(dailyRate, days);
    }

    /** The deposit this renter pays for this vehicle. */
    public Money depositFor(Renter renter) {
        return vehicleClass.deposit(deposit, renter.type());
    }

    /** Only the Depot, in this package, may change status. Refuses if not available. */
    void markRented(Rental rental) {
        if (!isAvailable()) {
            throw new IllegalStateException(model + " (" + reg + ") is not available");
        }
        status = new VehicleStatus.Rented(rental);
    }

    /** Only the Depot may call this. Refuses if the vehicle is not rented. */
    Rental markReturned() {
        Rental rental = currentRental().orElseThrow(
                () -> new IllegalStateException(model + " (" + reg + ") is not rented"));
        status = new VehicleStatus.Available();
        return rental;
    }
}