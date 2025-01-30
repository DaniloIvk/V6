package ivk.danilo.v6.Models.Base;

import org.jetbrains.annotations.NotNull;

public class MissingTableException extends RuntimeException {
    public MissingTableException(@NotNull Class<?> causer) {
        super("The '" + causer.getSimpleName() + "' model is missing table value!", null, false, false);
    }

    public MissingTableException() {
        super("The required model is missing table value!", null, false, false);
    }
}
