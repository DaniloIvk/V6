package ivk.danilo.v6.Models;

import android.database.Cursor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

import ivk.danilo.v6.Models.Base.Attribute.Attribute;
import ivk.danilo.v6.Models.Base.Attributes;
import ivk.danilo.v6.Models.Base.Model;
import ivk.danilo.v6.Models.Base.QueryBuilder;

public final class User extends Model {
    public User(@NotNull Attributes attributes) {
        super(attributes);
    }

    public User(@NotNull String databasePath) {
        super(databasePath);
    }

    @NotNull
    @Contract("_ -> new")
    public static QueryBuilder query(@NotNull String databasePath) {
        return new QueryBuilder(new User(databasePath));
    }

    @NotNull
    @Contract(pure = true)
    public String getTable() {
        return "users";
    }

    @NotNull
    @Override
    @Contract("_ -> new")
    protected User mapCursor(@NotNull Cursor cursor) {
        return new User(new Attributes(
                new Attribute("id", cursor.getColumnIndexOrThrow("id")),
                new Attribute("name", cursor.getString(cursor.getColumnIndexOrThrow("name"))),
                new Attribute("age", cursor.getInt(cursor.getColumnIndexOrThrow("age"))),
                new Attribute("department", cursor.getString(cursor.getColumnIndexOrThrow("department"))),
                new Attribute("gpa", cursor.getDouble(cursor.getColumnIndexOrThrow("gpa"))),
                new Attribute("created_at", new Date(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")))),
                new Attribute("updated_at", new Date(cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))))
        ));
    }
}
