package dovewing;

/** Economy cars. Uses the standard rules. */
public record Economy() implements VehicleClass {

    @Override
    public String label() {
        return "ECONOMY";
    }
}