package dovewing;

/**
 * The rulebook for one class of vehicle. It decides what a rental costs and
 * what deposit is taken. The defaults are the standard rules; a class with
 * different rules (for example a commercial van) overrides only what differs.
 */
public interface VehicleClass {

    /** The name shown on reports, e.g. "ECONOMY". */
    String label();

    /** Cost of renting for a number of days at the vehicle's daily rate. */
    default Money cost(Money dailyRate, int days) {
        return dailyRate.times(days);
    }

    /** Deposit taken, given the vehicle's listed deposit and who is renting. */
    default Money deposit(Money listedDeposit, RenterType renterType) {
        return renterType.waivesDeposit() ? Money.ZERO : listedDeposit;
    }
}