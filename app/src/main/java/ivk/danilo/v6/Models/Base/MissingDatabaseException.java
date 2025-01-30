package ivk.danilo.v6.Models.Base;

import org.jetbrains.annotations.NotNull;

public class MissingDatabaseException extends RuntimeException {
    public MissingDatabaseException(@NotNull Class<?> causer) {
        super("The '" + causer.getSimpleName() + "' model is missing database value!", null, false, false);
    }

    public MissingDatabaseException() {
        super("The required model is missing database value!", null, false, false);
    }
}
