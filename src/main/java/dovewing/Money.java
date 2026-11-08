package dovewing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** An amount of money in GHS. Stored exactly, never as a double. */
public record Money(BigDecimal amount) {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(amount, "amount");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(String text) {
        return new Money(new BigDecimal(text));
    }

    public Money times(int factor) {
        return new Money(amount.multiply(BigDecimal.valueOf(factor)));
    }

    /** Prints like the original program did: 450.0, 0.0, 1200.0. */
    @Override
    public String toString() {
        BigDecimal shown = amount.stripTrailingZeros();
        if (shown.scale() < 1) {
            shown = shown.setScale(1);
        }
        return shown.toPlainString();
    }
}