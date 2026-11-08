package dovewing;

/** Premium cars. Uses the standard rules today. */
public record Premium() implements VehicleClass {

    @Override
    public String label() {
        return "PREMIUM";
    }
}