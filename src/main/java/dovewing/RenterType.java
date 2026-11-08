package dovewing;

/** The kind of renter. Members have their deposit waived. */
public enum RenterType {
    CASUAL(false),
    MEMBER(true);

    private final boolean depositWaived;

    RenterType(boolean depositWaived) {
        this.depositWaived = depositWaived;
    }

    public boolean waivesDeposit() {
        return depositWaived;
    }
}