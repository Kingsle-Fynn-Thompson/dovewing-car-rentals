package dovewing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class DepotTest {

    private Vehicle newVitz() {
        return new Vehicle("Toyota Vitz", "GT-1234-20",
                Money.of("150.00"), Money.of("500.00"), new Economy());
    }

    @Test
    void refusesToRentAVehicleThatIsAlreadyRented() {
        Depot depot = new Depot("Test Depot");
        Vehicle vitz = newVitz();
        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addVehicle(vitz);
        depot.addRenter(kofi);
        depot.addRenter(ama);

        Optional<Rental> first = depot.rent(kofi, vitz, 3);
        Optional<Rental> second = depot.rent(ama, vitz, 2);

        assertTrue(first.isPresent(), "the first rental should succeed");
        assertTrue(second.isEmpty(), "the second rental must be refused");
        assertSame(first.get(), vitz.currentRental().orElseThrow(),
                "the vehicle must still belong to Kofi");
        assertEquals(1, depot.rentals().size(), "no record is created for a refused rental");
    }

    @Test
    void pricesMatchTheOriginalProgram() {
        Depot depot = new Depot("Test Depot");
        Vehicle vitz = newVitz();
        Vehicle prado = new Vehicle("Toyota Prado", "GR-5678-22",
                Money.of("600.00"), Money.of("2000.00"), new Premium());
        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addVehicle(vitz);
        depot.addVehicle(prado);
        depot.addRenter(kofi);
        depot.addRenter(ama);

        Rental casualEconomy = depot.rent(kofi, vitz, 3).orElseThrow();
        Rental memberPremium = depot.rent(ama, prado, 2).orElseThrow();

        assertEquals(Money.of("450.00"), casualEconomy.cost());
        assertEquals(Money.of("500.00"), casualEconomy.deposit());
        assertEquals(Money.of("1200.00"), memberPremium.cost());
        assertEquals(Money.ZERO, memberPremium.deposit(), "members have the deposit waived");
    }

    @Test
    void returningFreesTheVehicleAndClosesOnlyThatRental() {
        Depot depot = new Depot("Test Depot");
        Vehicle vitz = newVitz();
        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addVehicle(vitz);
        depot.addRenter(kofi);
        depot.addRenter(ama);

        Rental first = depot.rent(kofi, vitz, 3).orElseThrow();
        assertTrue(depot.returnVehicle(vitz));

        assertTrue(vitz.isAvailable());
        assertFalse(first.isActive(), "the returned rental is closed");

        Rental second = depot.rent(ama, vitz, 2).orElseThrow();
        assertTrue(second.isActive(), "the vehicle can be rented again after a return");
        assertFalse(first.isActive(), "the old rental stays closed");
    }

    @Test
    void refusesToReturnAVehicleThatIsNotRented() {
        Depot depot = new Depot("Test Depot");
        Vehicle vitz = newVitz();
        depot.addVehicle(vitz);

        assertFalse(depot.returnVehicle(vitz));
        assertTrue(vitz.isAvailable());
    }
}