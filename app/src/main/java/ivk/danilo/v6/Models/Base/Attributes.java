package ivk.danilo.v6.Models.Base;

import android.content.ContentValues;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ivk.danilo.v6.Models.Base.Attribute.Attribute;
import ivk.danilo.v6.Models.Base.Attribute.MissingValue;
import ivk.danilo.v6.Models.Base.Attribute.MissingValueException;

public class Attributes {
    private final List<Attribute> attributes;
    private final ContentValues contentValues;

    public Attributes(@NotNull List<Attribute> attributes) {
        this.attributes = new ArrayList<>();
        this.contentValues = new ContentValues();

        for (Attribute attribute : attributes) {
            this.set(attribute.getName(), attribute.getValue());
        }
    }

    public Attributes(Attribute... attributes) {
        this(Arrays.asList(attributes));
    }

    public Attributes() {
        this(new Attribute[0]);
    }

    @NotNull
    public Attribute get(String name) throws MissingValueException {
        if (this.doesntHave(name)) {
            throw new MissingValueException(name);
        }

        return this.find(name);
    }

    public void set(@NotNull String name, @Nullable Object value) {
        Attribute attribute = new Attribute(name, value);

        if (this.doesntHave(name)) {
            this.attributes.add(attribute);
            this.addContextValue(name, value);
            return;
        }

        this.attributes.set(this.indexOf(name), attribute);
    }

    @NotNull
    public Attribute find(@NotNull String name) {
        for (Attribute attribute : this.attributes) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }

        return new MissingValue();
    }

    public boolean has(@NotNull String name) {
        return !(this.find(name) instanceof MissingValue);
    }

    public boolean doesntHave(@NotNull String name) {
        return this.find(name) instanceof MissingValue;
    }

    @NotNull
    @Contract(pure = true)
    public Attribute[] toArray() {
        return this.attributes.toArray(new Attribute[0]);
    }

    public int indexOf(@NotNull String name) {
        return this.attributes.indexOf(this.find(name));
    }

    public ContentValues toContentValues() {
        return this.contentValues;
    }

    private void addContextValue(@NotNull String name, @Nullable Object value) {
        if (value == null) {
            this.contentValues.putNull(name);
            return;
        }

        switch (value.getClass().getSimpleName()) {
            case "String":
                this.contentValues.put(name, (String) value);
                break;
            case "Long":
                this.contentValues.put(name, (Long) value);
                break;
            case "Double":
                this.contentValues.put(name, (Double) value);
                break;
            case "Float":
                this.contentValues.put(name, (Float) value);
                break;
            case "Integer":
                this.contentValues.put(name, (Integer) value);
                break;
            case "Short":
                this.contentValues.put(name, (Short) value);
                break;
            case "Byte":
                this.contentValues.put(name, (Byte) value);
                break;
            case "Boolean":
                this.contentValues.put(name, (Boolean) value);
                break;
            case "Date":
                this.contentValues.put(name, ((Date) value).getTime());
                break;
            default:
                this.contentValues.putNull(name);
        }
    }
}
