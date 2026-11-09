# Dovewing Car Rentals: fleet and rental tracker (rebuild)

A rebuild of the Dovewing depot's fleet tracker. It does the same job as the
original, but a vehicle can no longer be both available and rented, and a new
vehicle class (the planned commercial van) can be added without touching
existing code.

Start with [DIAGNOSIS.md](DIAGNOSIS.md), which was committed before any code changed.

## Running it

Requires JDK 21.

```
javac -d target/classes src/main/java/dovewing/*.java
java -cp target/classes dovewing.Main
```

Tests (JUnit 5): `mvn test`, or run them from IntelliJ.

The original program is still in `src/main/java/RentalTracker.java` so the two
can be compared. `baseline-output.txt` is its output; `rebuilt-output.txt` is the
rebuilt program's output.

## The structure

| Class | Job |
|---|---|
| `Money` | An exact amount (BigDecimal), immutable. Prints like the original (`450.0`). |
| `RenterType` | Enum: `CASUAL` or `MEMBER`. States whether the deposit is waived. |
| `VehicleClass` | Interface holding the pricing and deposit rules. `Economy` and `Premium` implement it. |
| `VehicleStatus` | Sealed interface: `Available`, or `Rented` carrying the rental that holds the car. |
| `Vehicle` | Owns its status. Only `markRented` and `markReturned` change it, and both refuse invalid changes. |
| `Renter` | Name and type, fixed once created. History is read-only from outside. |
| `Rental` | An immutable record of one rental. It has no status of its own. |
| `Depot` | Owns the fleet, renters and rentals. Refuses unavailable rentals and invalid returns. |

## Key design decisions

**1. One status per vehicle, as a sealed type.**
The original kept "is this car free?" in three unconnected places (the vehicle's
status string, each rental's status string, the rentals list). Any of them could
disagree. Now a vehicle holds one `VehicleStatus`, either `Available` or
`Rented(rental)`. A rented car always knows which rental holds it, and a car cannot
be both. I chose a sealed interface over an enum plus a nullable field because it
makes the contradictory state impossible to build, rather than something to check for.

**2. The guard lives in the vehicle, not only in the depot.**
`Depot.rent` checks availability first, but `Vehicle.markRented` also refuses if the
car is not available, and it is package-private. Even a future bug in `Depot` cannot
double-rent a car, and code outside the package cannot change status at all.

**3. A rental has no status of its own.**
`Rental.isActive()` asks the vehicle whether its current rental is this one. There is
one source of truth, so a rental and a vehicle cannot disagree.

**4. Pricing rules sit behind an interface, not an enum or `if/else`.**
The original had two identical copy-pasted blocks for economy and premium. Now
`VehicleClass` holds the standard rules as defaults and each class supplies its
label. I chose an open interface over an enum because adding a class to an enum means
editing the enum, while a new interface implementation is a new file. `VehicleStatus`
is sealed because there the set really is closed, and `VehicleClass` is not because
it must stay open. Knowing which to use where was the point.

**5. Vehicle class and renter type are separate.**
The deposit is a function of both. `RenterType` only states a fact ("members are
waived"); the vehicle class decides what to do with it. A van with different deposit
rules overrides one method and the renter type never needs editing.

**6. Exact money.**
`Money` wraps `BigDecimal`, is built from text, and is immutable. Its `toString`
deliberately prints `450.0` rather than `450.00`, because the original printed
`double` values like that and the rebuild must produce identical output.

**7. Encapsulation.**
All fields are private and final where they can be. `Depot` returns read-only copies
of its lists. Constructors validate: blank names, missing values and rentals under
1 day are refused instead of creating broken records.

## What changed from my original sketch (and why)

- **`Rental` has no status.** The sketch gave it a `RentalState`. While building
  I saw that this recreated the original bug: a second copy of "is this car out?"
  that could disagree with the vehicle. `Rental.isActive()` now asks the vehicle.
- **`rent()` returns `Optional<Rental>`, not a `RentResult`.** A refusal is a normal
  outcome, not an error, and `Optional` says "a rental, or none" without a
  new type. `main` carries on exactly as the original did.
- **`Vehicle` and `Renter` have no `id` field.** The original never printed or read
  them, and their global counters were a weakness. Registration and object identity
  replace them. Rental numbers are kept because the report prints them.

## Adding the commercial van

With this design: one new file, a class implementing `VehicleClass` that overrides
only the deposit rule. `Depot`, `Vehicle`, `Economy`, `Premium` and `RenterType` do
not change. `CommercialVanTest` demonstrates this.

With the old code, a van would have needed a third copy of the `if/else` pricing
block in `rent()`, a change to every place that compares the category string, and
edits to the same method that already handled economy and premium, which is how a
premium tweak once broke economy.

Note: the brief says vans will have their own deposit rules but not what they are.
The rule in `CommercialVanTest` (members still pay the deposit) is an example that
shows the mechanism, not a real business rule.

## Behaviour preservation

I ran the original and saved its output as `baseline-output.txt`, then ran the
rebuilt `Main` on the same scenario and compared them. They differ in exactly three
lines, all from the one rule the business wanted enforced: the original accepted
Ama renting the Vitz while Kofi had it, so its report showed two active rentals on
one car; the rebuild prints a refusal and shows one.

Everything else matches: the "Rented" and "Returned" lines, prices and deposits,
and the fleet status. `DepotTest.pricesMatchTheOriginalProgram` keeps those numbers
checked automatically.

Deliberate differences, all outside what the demo exercises:
- Refused operations print a message (`Cannot rent ...`, `Cannot return ...`) and
  return a result instead of silently succeeding. The wording is new.
- A refused rental does not use up a rental number.
- Rentals under 1 day, blank names and unknown renters or vehicles are refused.
- The internal `id` fields on `Vehicle` and `Renter` were removed: the original
  never printed or read them, and their global counters were a weakness. Rental
  numbers (which the report prints) are kept, and now belong to the depot.

## My one improvement: a test suite

Six JUnit tests that lock in the baseline numbers, protect the never-both rule,
cover returns, and prove the van claim. This helps the business because a future
change that reintroduces the double-rental bug fails a test instead of surfacing at
the front desk, and it helps the next developer because they can change the code and
know quickly whether behaviour was preserved.