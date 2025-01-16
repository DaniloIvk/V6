package ivk.danilo.v6.Models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ivk.danilo.v6.Sqlite.DatabaseHelper;

public final class User extends Model {
    public final String table = "users";
    public final long id;
    public final String name;
    public final int age;
    public final String department;
    public final double gpa;
    public final String createdAt;
    public final String updatedAt;

    public User(long id, String name, int age, String department, double gpa, String createdAt, String updatedAt) {
        super(null);
        this.id = id;
        this.name = name;
        this.age = age;
        this.department = department;
        this.gpa = gpa;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User(long id, String name, int age, String department, double gpa, String createdAt) {
        this(id, name, age, department, gpa, createdAt, "");
    }

    public User(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
        this.id = -1;
        this.name = "";
        this.age = -1;
        this.department = "";
        this.gpa = -1;
        this.createdAt = "";
        this.updatedAt = "";
    }

    public static User newQuery(@NotNull final DatabaseHelper databaseHelper) {
        return new User(databaseHelper.openDatabase());
    }

    public User create(String name, int age, String department, double gpa) {
        Date createdAt = new Date();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("age", age);
        contentValues.put("department", department);
        contentValues.put("gpa", gpa);
        contentValues.put("created_at", createdAt.getTime());
        contentValues.put("updated_at", createdAt.getTime());

        long id = this.database.insert(this.table, null, contentValues);

        this.database.close();

        return new User(id, name, age, department, gpa, createdAt.toString());
    }

    @NonNull
    public List<User> getAll() {
        List<User> users = new ArrayList<>();

        Cursor cursor = this.database.query(this.table, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                users.add(this.mapCursorToUser(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        this.database.close();

        return users;
    }

    public List<User> getTop3() {
        String query = "SELECT * FROM " + this.table + " ORDER BY gpa DESC LIMIT 3";
        Cursor cursor = this.database.rawQuery(query, null);
        List<User> topUsers = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                topUsers.add(this.mapCursorToUser(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return topUsers;
    }

    private User mapCursorToUser(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        int age = cursor.getInt(cursor.getColumnIndexOrThrow("age"));
        String department = cursor.getString(cursor.getColumnIndexOrThrow("department"));
        double gpa = cursor.getDouble(cursor.getColumnIndexOrThrow("gpa"));
        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"));
        long updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"));

        return new User(id, name, age, department, gpa, new Date(createdAt).toString(), new Date(updatedAt).toString());
    }
}
