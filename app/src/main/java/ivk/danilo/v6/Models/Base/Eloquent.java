package ivk.danilo.v6.Models.Base;

import android.database.sqlite.SQLiteDatabase;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ivk.danilo.v6.Models.Base.Attribute.Attribute;
import ivk.danilo.v6.Models.Base.Attribute.MissingValueException;

public class Eloquent {
    protected final String databaseName = "database.sqlite";
    protected final SQLiteDatabase sqLiteDatabase;
    protected final Attributes attributes;
    protected QueryBuilder queryBuilder = null;

    public Eloquent(@Nullable Attributes attributes, @Nullable String databasePath) {
        this.attributes = attributes;

        if (databasePath == null) {
            this.sqLiteDatabase = null;
            return;
        }

        this.sqLiteDatabase = SQLiteDatabase.openDatabase(databasePath + "/" + this.databaseName, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @NotNull
    @Contract(pure = true)
    public static String getPrimaryKey() {
        return "id";
    }

    @NotNull
    public String getTable() throws MissingTableException {
        throw new MissingTableException(Eloquent.class);
    }

    public Attribute id() throws MissingValueException {
        return this.getAttribute(getPrimaryKey());
    }

    public Attribute getAttribute(@NotNull String name) throws MissingValueException {
        return this.attributes.get(name);
    }

    @Contract(pure = true)
    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public String getWhereClauseForThisModel() {
        return new QueryBuilder()
                .where(getPrimaryKey(), this.id().toLong())
                .toString();
    }
}
