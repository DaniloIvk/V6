package ivk.danilo.v6.Models;

import android.database.sqlite.SQLiteDatabase;

import org.jetbrains.annotations.NotNull;

import ivk.danilo.v6.Sqlite.DatabaseHelper;

public class Model {
    protected final SQLiteDatabase database;
    protected String table;

    protected Model(SQLiteDatabase sqLiteDatabase) {
        this.database = sqLiteDatabase;
    }

    public static Model newQuery(@NotNull final DatabaseHelper databaseHelper, String tableName) {
        SQLiteDatabase db = databaseHelper.openDatabase();
        Model model = new Model(db);
        model.table = tableName;
        return model;
    }
}
