package dovewing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EdgeCaseTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream captured;

    private Depot depot;
    private Vehicle vitz;
    private Vehicle prado;
    private Renter kofi;
    private Renter ama;

    @BeforeEach
    void setUp() {
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));

        depot = new Depot("Test Depot");
        vitz = new Vehicle("Toyota Vitz", "GT-1234-20",
                Money.of("150.00"), Money.of("500.00"), new Economy());
        prado = new Vehicle("Toyota Prado", "GR-5678-22",
                Money.of("600.00"), Money.of("2000.00"), new Premium());
        kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addVehicle(vitz);
        depot.addVehicle(prado);
        depot.addRenter(kofi);
        depot.addRenter(ama);
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    void refusedRentalPrintsTheRefusalMessage() {
        depot.rent(kofi, vitz, 3);
        captured.reset();

        depot.rent(ama, vitz, 2);

        assertEquals("Cannot rent Toyota Vitz (GT-1234-20) to Ama Owusu: it is already rented",
                captured.toString().trim());
    }

    @Test
    void refusedReturnPrintsTheRefusalMessage() {
        depot.returnVehicle(vitz);

        assertEquals("Cannot return Toyota Vitz (GT-1234-20): it is not rented",
                captured.toString().trim());
    }

    @Test
    void aRefusedRentalDoesNotUseUpARentalNumber() {
        Rental first = depot.rent(kofi, vitz, 3).orElseThrow();
        assertTrue(depot.rent(ama, vitz, 2).isEmpty());
        Rental next = depot.rent(ama, prado, 2).orElseThrow();

        assertEquals(1, first.id());
        assertEquals(2, next.id(), "the refused attempt must not consume number 2");
    }

    @Test
    void rentalsUnderOneDayAreRefusedAndChangeNothing() {
        assertThrows(IllegalArgumentException.class, () -> depot.rent(kofi, vitz, 0));
        assertThrows(IllegalArgumentException.class, () -> depot.rent(kofi, vitz, -2));

        assertTrue(vitz.isAvailable(), "a refused rental leaves the vehicle available");
        assertTrue(depot.rentals().isEmpty(), "and creates no record");
    }

    @Test
    void anUnregisteredRenterIsRefused() {
        Renter stranger = new Renter("Stranger", RenterType.CASUAL);

        assertThrows(IllegalArgumentException.class, () -> depot.rent(stranger, vitz, 1));
        assertTrue(vitz.isAvailable());
    }

    @Test
    void blankNamesAreRefused() {
        assertThrows(IllegalArgumentException.class,
                () -> new Renter("  ", RenterType.CASUAL));
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(" ", "GT-0000-00", Money.ZERO, Money.ZERO, new Economy()));
    }

    @Test
    void moneyPrintsLikeTheOriginalProgram() {
        assertEquals("450.0", Money.of("450.00").toString());
        assertEquals("1200.0", Money.of("600").times(2).toString());
        assertEquals("0.0", Money.ZERO.toString());
        assertEquals("150.5", Money.of("150.50").toString());
    }

    @Test
    void equalAmountsAreEqualWhateverHowTheyAreWritten() {
        assertEquals(Money.of("150"), Money.of("150.00"));
    }
}