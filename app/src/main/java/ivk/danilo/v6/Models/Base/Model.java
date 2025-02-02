package ivk.danilo.v6.Models.Base;

import android.database.Cursor;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Model extends Eloquent {
    protected Model(@Nullable Attributes attributes, @NotNull String databasePath) {
        super(attributes, databasePath);
    }

    public Model(@NotNull Attributes attributes) {
        super(attributes, null);
    }

    protected Model(@NotNull String databasePath) {
        super(new Attributes(), databasePath);
    }

    protected Model() {
        super(new Attributes(), null);
    }

    // <editor-fold desc="CRUD functions">
    @NotNull
    @Contract(" -> new")
    public List<Model> get() {
       this.validateDatabaseConnection();

        if (this.queryBuilder == null) {
            this.queryBuilder = new QueryBuilder(this);
        }

        List<Model> results = new ArrayList<>();

        assert this.sqLiteDatabase != null;
        Cursor cursor = this.sqLiteDatabase.rawQuery(this.queryBuilder.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                results.add(this.mapCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        this.sqLiteDatabase.close();

        return results;
    }

    @NotNull
    @Contract("_ -> this")
    public Model create(@NonNull Attributes attributes) {
        this.validateDatabaseConnection();

        assert this.sqLiteDatabase != null;
        Long id = this.sqLiteDatabase.insert(this.getTable(), null, attributes.toContentValues());

        attributes.set(getPrimaryKey(), id);

        this.sqLiteDatabase.close();

        return new Model(attributes);
    }

    @NotNull
    @Contract("_ -> this")
    public Model update(@NotNull Attributes attributes) {
        this.validateDatabaseConnection();

        attributes.set("updated_at", new Date().getTime());

        assert this.sqLiteDatabase != null;
        this.sqLiteDatabase.update(this.getTable(), attributes.toContentValues(), this.getWhereClauseForThisModel(), null);

        this.sqLiteDatabase.close();

        return new Model(attributes);
    }

    @Contract(pure = true)
    public void delete() {
        this.validateDatabaseConnection();

        assert this.sqLiteDatabase != null;
        this.sqLiteDatabase.delete(this.getTable(), this.getWhereClauseForThisModel(), null);

        this.sqLiteDatabase.close();
    }

    public String[] pluck(@NotNull String column) {
        this.validateDatabaseConnection();

        if (this.queryBuilder == null) {
            this.queryBuilder = new QueryBuilder(this);
        }

        List<String> results = new ArrayList<>();

        assert this.sqLiteDatabase != null;
        Cursor cursor = this.sqLiteDatabase.rawQuery(this.queryBuilder.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                results.add(cursor.getString(cursor.getColumnIndexOrThrow(column)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        this.sqLiteDatabase.close();

        return results.toArray(new String[0]);
    }
    // </editor-fold> CRUD functions

    protected Model mapCursor(Cursor cursor) {
        return new Model();
    }

    protected void validateDatabaseConnection() {
        if (this.sqLiteDatabase == null) {
            throw new MissingDatabaseException(this.getClass());
        }
    }
}
