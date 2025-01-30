package ivk.danilo.v6.Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.StringRes;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ivk.danilo.v6.R;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database.sqlite";
    private static String DATABASE_PATH = "";
    private final Context context;

    public DatabaseHelper(@NotNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;

        DatabaseHelper.DATABASE_PATH = context.getFilesDir().getPath() + "/" + DATABASE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Toast.makeText(this.context, R.string.app_name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Toast.makeText(this.context, R.string.app_name, Toast.LENGTH_SHORT).show();
    }

    public void copyDatabase() {
        try {
            File databaseFile = new File(DATABASE_PATH);

            if (databaseFile.exists()) {
                throw new Exception(this.getString(R.string.database_exists));
            }

            InputStream inputStream = context.getAssets().open(DATABASE_NAME);
            OutputStream outputStream = new FileOutputStream(DATABASE_PATH);

            byte[] buffer = new byte[1024];

            for (int length; (length = inputStream.read(buffer)) > 0; ) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception exception) {
            Toast.makeText(this.context, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public SQLiteDatabase openDatabase() {
        try {
            return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Get string resource value using context
     *
     * @param resourceId String resource id
     * @return Resource value
     */
    @NotNull
    private String getString(@StringRes int resourceId) {
        return this.context.getString(resourceId);
    }
}
