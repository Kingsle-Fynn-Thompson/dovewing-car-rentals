package dovewing;

/** Replays the original program's scenario against the rebuilt classes. */
public class Main {

    public static void main(String[] args) {
        Depot d = new Depot("Dovewing Car Rentals");

        Vehicle vitz = new Vehicle("Toyota Vitz", "GT-1234-20",
                Money.of("150.00"), Money.of("500.00"), new Economy());
        Vehicle prado = new Vehicle("Toyota Prado", "GR-5678-22",
                Money.of("600.00"), Money.of("2000.00"), new Premium());
        d.addVehicle(vitz);
        d.addVehicle(prado);

        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        d.addRenter(kofi);
        d.addRenter(ama);

        // a normal day
        d.rent(kofi, vitz, 3);
        d.rent(ama, prado, 2);
        d.returnVehicle(prado);

        // the same bad request as the original: the Vitz is already out with Kofi.
        // The old program accepted this; the rebuilt one refuses it.
        d.rent(ama, vitz, 2);

        d.printReport();
    }
}