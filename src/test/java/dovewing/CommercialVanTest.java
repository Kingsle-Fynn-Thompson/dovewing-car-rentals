package dovewing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CommercialVanTest {

    /**
     * A brand-new vehicle class, written here as one small file's worth of code.
     * The rule is an example: a van's deposit is never waived, even for members.
     * Cost and everything else come from the VehicleClass defaults.
     */
    private record CommercialVan() implements VehicleClass {
        @Override
        public String label() {
            return "VAN";
        }

        @Override
        public Money deposit(Money listedDeposit, RenterType renterType) {
            return listedDeposit;
        }
    }

    @Test
    void aNewVehicleClassWorksWithoutChangingDepotOrVehicle() {
        Depot depot = new Depot("Test Depot");
        Vehicle van = new Vehicle("Toyota Hiace", "GW-9012-23",
                Money.of("400.00"), Money.of("3000.00"), new CommercialVan());
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addVehicle(van);
        depot.addRenter(ama);

        Rental rental = depot.rent(ama, van, 2).orElseThrow();

        assertEquals(Money.of("800.00"), rental.cost(), "cost uses the standard rule");
        assertEquals(Money.of("3000.00"), rental.deposit(),
                "the van's own rule: a member still pays the deposit");
        assertEquals("VAN", van.vehicleClass().label());
        assertTrue(depot.rent(ama, van, 1).isEmpty(),
                "the never-both rule protects vans like any other vehicle");
    }

    @Test
    void existingClassesAreUnaffectedByTheNewOne() {
        Depot depot = new Depot("Test Depot");
        Vehicle vitz = new Vehicle("Toyota Vitz", "GT-1234-20",
                Money.of("150.00"), Money.of("500.00"), new Economy());
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addVehicle(vitz);
        depot.addRenter(ama);

        Rental rental = depot.rent(ama, vitz, 2).orElseThrow();

        assertEquals(Money.ZERO, rental.deposit(), "members still have economy deposits waived");
    }
}