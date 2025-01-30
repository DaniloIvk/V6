package ivk.danilo.v6.Models.Base.Attribute;

import org.jetbrains.annotations.Contract;

public final class MissingValue extends Attribute {
    private static final String MISSING_VALUE = "MISSING_VALUE";

    public MissingValue() {
        super(MISSING_VALUE, null);
    }

    @Contract(pure = true)
    public String getValue() {
        throw new MissingValueException();
    }
}