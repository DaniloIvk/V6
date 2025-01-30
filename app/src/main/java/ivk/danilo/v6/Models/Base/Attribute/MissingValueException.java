package ivk.danilo.v6.Models.Base.Attribute;

public class MissingValueException extends RuntimeException {
    public MissingValueException(String attributeName) {
        super("The '" + attributeName + "' attribute has missing value!", null, false, false);
    }

    public MissingValueException() {
        super("The attribute has missing value!", null, false, false);
    }
}
