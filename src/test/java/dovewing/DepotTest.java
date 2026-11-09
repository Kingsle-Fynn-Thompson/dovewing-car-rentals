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
}