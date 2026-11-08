package dovewing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** A customer of the depot. Name and type are fixed once created. */
public final class Renter {

    private final String name;
    private final RenterType type;
    private final List<String> history = new ArrayList<>();

    public Renter(String name, RenterType type) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("A renter needs a name");
        }
        this.name = name;
        this.type = Objects.requireNonNull(type, "type");
    }

    public String name() {
        return name;
    }

    public RenterType type() {
        return type;
    }

    /** A read-only copy of what this renter has rented. */
    public List<String> history() {
        return List.copyOf(history);
    }

    /** Only the Depot, in this package, records history. */
    void recordRental(String vehicleModel) {
        history.add("Rented " + vehicleModel);
    }
}