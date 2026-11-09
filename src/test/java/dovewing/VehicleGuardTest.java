package dovewing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VehicleGuardTest {

    private Vehicle vitz() {
        return new Vehicle("Toyota Vitz", "GT-1234-20",
                Money.of("150.00"), Money.of("500.00"), new Economy());
    }

    private Vehicle prado() {
        return new Vehicle("Toyota Prado", "GR-5678-22",
                Money.of("600.00"), Money.of("2000.00"), new Premium());
    }

    @Test
    void aRentalForAnotherVehicleCannotBeAttached() {
        Vehicle vitz = vitz();
        Vehicle prado = prado();
        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Rental pradoRental = new Rental(1, kofi, prado, 2, Money.of("1200.00"), Money.ZERO);

        assertThrows(IllegalArgumentException.class, () -> vitz.markRented(pradoRental));
        assertTrue(vitz.isAvailable(), "a refused change leaves the status untouched");
    }

    @Test
    void aVehicleCannotBeMarkedRentedTwice() {
        Vehicle vitz = vitz();
        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Rental first = new Rental(1, kofi, vitz, 3, Money.of("450.00"), Money.of("500.00"));
        Rental second = new Rental(2, kofi, vitz, 2, Money.of("300.00"), Money.of("500.00"));
        vitz.markRented(first);

        assertThrows(IllegalStateException.class, () -> vitz.markRented(second));
        assertSame(first, vitz.currentRental().orElseThrow(), "the first rental still holds the car");
    }

    @Test
    void aVehicleThatIsNotRentedCannotBeReturned() {
        assertThrows(IllegalStateException.class, () -> vitz().markReturned());
    }
}
