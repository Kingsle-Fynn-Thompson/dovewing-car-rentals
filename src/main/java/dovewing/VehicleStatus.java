package dovewing;

import java.util.Objects;

/**
 * The status of a vehicle: exactly one of two shapes. A vehicle is either
 * Available, or Rented with the rental that holds it. It can never be both.
 */
public sealed interface VehicleStatus {

    /** The word shown on reports: AVAILABLE or RENTED. */
    String label();

    /** A vehicle that is free to rent. */
    record Available() implements VehicleStatus {
        @Override
        public String label() {
            return "AVAILABLE";
        }
    }

    /** A vehicle that is out, always tied to the rental that holds it. */
    record Rented(Rental rental) implements VehicleStatus {
        public Rented {
            Objects.requireNonNull(rental, "rental");
        }

        @Override
        public String label() {
            return "RENTED";
        }
    }
}