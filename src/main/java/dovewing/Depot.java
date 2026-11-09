package dovewing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** The depot: owns the fleet, the renters and the rental records. */
public final class Depot {

    private final String name;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Renter> renters = new ArrayList<>();
    private final List<Rental> rentals = new ArrayList<>();
    private int nextRentalId = 1;

    public Depot(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String name() {
        return name;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(Objects.requireNonNull(vehicle, "vehicle"));
    }

    public void addRenter(Renter renter) {
        renters.add(Objects.requireNonNull(renter, "renter"));
    }

    public List<Vehicle> vehicles() {
        return List.copyOf(vehicles);
    }

    public List<Renter> renters() {
        return List.copyOf(renters);
    }

    public List<Rental> rentals() {
        return List.copyOf(rentals);
    }

    /**
     * Rents a vehicle. If the vehicle is not available the rental is refused:
     * a message is printed and nothing changes.
     */
    public Optional<Rental> rent(Renter renter, Vehicle vehicle, int days) {
        requireKnown(renter, vehicle);
        if (!vehicle.isAvailable()) {
            System.out.println("Cannot rent " + vehicle.model() + " (" + vehicle.reg()
                    + ") to " + renter.name() + ": it is already rented");
            return Optional.empty();
        }
        Money cost = vehicle.costFor(days);
        Money deposit = vehicle.depositFor(renter);
        Rental rental = new Rental(nextRentalId, renter, vehicle, days, cost, deposit);
        vehicle.markRented(rental);
        nextRentalId++;
        renter.recordRental(vehicle.model());
        rentals.add(rental);
        System.out.println("Rented " + vehicle.model() + " (" + vehicle.reg() + ") to "
                + renter.name() + " for " + days + " days, cost GHS " + cost
                + ", deposit GHS " + deposit);
        return Optional.of(rental);
    }

    /** Returns a vehicle. Refused, with a message, if it is not currently rented. */
    public boolean returnVehicle(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "vehicle");
        if (!vehicles.contains(vehicle)) {
            throw new IllegalArgumentException("That vehicle is not in this depot's fleet");
        }
        if (vehicle.isAvailable()) {
            System.out.println("Cannot return " + vehicle.model() + " (" + vehicle.reg()
                    + "): it is not rented");
            return false;
        }
        vehicle.markReturned();
        System.out.println("Returned " + vehicle.model() + " (" + vehicle.reg() + ")");
        return true;
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Fleet status:");
        for (Vehicle v : vehicles) {
            System.out.println("  " + v.model() + " (" + v.reg() + ", "
                    + v.vehicleClass().label() + "): " + v.status().label());
        }
        System.out.println("Active rentals:");
        for (Rental rt : rentals) {
            if (rt.isActive()) {
                System.out.println("  Rental #" + rt.id() + ": " + rt.vehicle().reg()
                        + " -> " + rt.renter().name() + " (" + rt.days() + " days)");
            }
        }
    }

    private void requireKnown(Renter renter, Vehicle vehicle) {
        Objects.requireNonNull(renter, "renter");
        Objects.requireNonNull(vehicle, "vehicle");
        if (!renters.contains(renter)) {
            throw new IllegalArgumentException(renter.name() + " is not a registered renter");
        }
        if (!vehicles.contains(vehicle)) {
            throw new IllegalArgumentException("That vehicle is not in this depot's fleet");
        }
    }
}