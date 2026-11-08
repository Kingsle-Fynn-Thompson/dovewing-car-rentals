package dovewing;

import java.util.Objects;

/** The record of one rental. Immutable; it never stores its own status. */
public final class Rental {

    private final int id;
    private final Renter renter;
    private final Vehicle vehicle;
    private final int days;
    private final Money cost;
    private final Money deposit;

    /** Created only by the Depot, in this package. */
    Rental(int id, Renter renter, Vehicle vehicle, int days, Money cost, Money deposit) {
        if (days < 1) {
            throw new IllegalArgumentException("A rental must be for at least 1 day");
        }
        this.id = id;
        this.renter = Objects.requireNonNull(renter, "renter");
        this.vehicle = Objects.requireNonNull(vehicle, "vehicle");
        this.days = days;
        this.cost = Objects.requireNonNull(cost, "cost");
        this.deposit = Objects.requireNonNull(deposit, "deposit");
    }

    public int id() { return id; }
    public Renter renter() { return renter; }
    public Vehicle vehicle() { return vehicle; }
    public int days() { return days; }
    public Money cost() { return cost; }
    public Money deposit() { return deposit; }

    /** Active means: the vehicle's current rental is this one. */
    public boolean isActive() {
        return vehicle.currentRental().filter(r -> r == this).isPresent();
    }
}