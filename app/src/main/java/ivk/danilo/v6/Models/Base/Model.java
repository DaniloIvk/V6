package ivk.danilo.v6.Models.Base;

import android.database.Cursor;

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
        if (this.sqLiteDatabase == null) {
            throw new MissingDatabaseException(this.getClass());
        }

        if (this.queryBuilder == null) {
            this.queryBuilder = new QueryBuilder(this);
        }

        List<Model> results = new ArrayList<>();

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
    public Model create(Attributes attributes) {
        if (this.sqLiteDatabase == null) {
            throw new MissingDatabaseException(this.getClass());
        }

        Long id = this.sqLiteDatabase.insert(this.getTable(), null, attributes.toContentValues());

        attributes.set(this.getPrimaryKey(), id);

        this.sqLiteDatabase.close();

        return new Model(attributes);
    }

    @NotNull
    @Contract("_ -> this")
    public Model update(Attributes attributes) {
        if (this.sqLiteDatabase == null) {
            throw new MissingDatabaseException(this.getClass());
        }

        attributes.set("updated_at", new Date().getTime());

        this.sqLiteDatabase.update(this.getTable(), attributes.toContentValues(), this.getWhereClauseForThisModel(), null);

        this.sqLiteDatabase.close();

        return new Model(attributes);
    }

    @Contract(pure = true)
    public void delete() {
        if (this.sqLiteDatabase == null) {
            throw new MissingDatabaseException(this.getClass());
        }

        this.sqLiteDatabase.delete(this.getTable(), this.getWhereClauseForThisModel(), null);

        this.sqLiteDatabase.close();
    }
    // </editor-fold> CRUD functions

    protected Model mapCursor(Cursor cursor) {
        return new Model();
    }
}
