package ivk.danilo.v6.Models.Base.Attribute;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class Attribute {
    @NotNull
    protected final String name;
    @Nullable
    protected final Object value;

    public Attribute(@NotNull String name, @Nullable Object value) {
        this.name = name;
        this.value = value;
    }

    @NonNull
    @Contract(pure = true)
    public String getName() {
        return this.name;
    }

    @Nullable
    @Contract(pure = true)
    public Object getValue() {
        return this.value;
    }

    @NotNull
    @Override
    public String toString() {
        if (this.value == null) {
            return "NULL";
        }

        return this.value.toString();
    }

    public Long toLong() {
        if (!(this.value instanceof Number)) {
            if (this.value instanceof Date) {
                return ((Date) this.value).getTime();
            }

            return null;
        }

        return ((Number) this.value).longValue();
    }

    public Double toDouble() {
        if (!(this.value instanceof Number)) {
            return null;
        }

        return ((Number) this.value).doubleValue();
    }

    public Float toFloat() {
        if (!(this.value instanceof Number)) {
            return null;
        }

        return ((Number) this.value).floatValue();
    }

    public Integer toInt() {
        if (!(this.value instanceof Number)) {
            return null;
        }

        return ((Number) this.value).intValue();
    }

    public Short toShort() {
        if (!(this.value instanceof Number)) {
            return null;
        }

        return ((Number) this.value).shortValue();
    }

    public Byte toByte() {
        if (!(this.value instanceof Number)) {
            return null;
        }

        return ((Number) this.value).byteValue();
    }

    public Boolean toBoolean() {
        if (!(this.value instanceof Boolean)) {
            if (this.value instanceof Number) {
                return ((Double) this.value) > 0;
            }

            if (this.value instanceof String) {
                return !this.value.toString().isEmpty();
            }

            return false;
        }

        return (Boolean) this.value;
    }

    public Date toDate() {
        if (!(this.value instanceof Date)) {
            if (this.value instanceof Long) {
                return new Date((Long) this.value);
            }

            return null;
        }

        return (Date) this.value;
    }
}
