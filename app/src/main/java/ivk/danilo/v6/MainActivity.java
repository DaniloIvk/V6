package ivk.danilo.v6;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ivk.danilo.v6.Adapters.UserAdapter;
import ivk.danilo.v6.Models.User;
import ivk.danilo.v6.Sqlite.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper = null;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Spinner studentDepartmentSpinner = findViewById(R.id.student_department);
        ArrayAdapter<String> studentDepartmentSpinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"SRT", "KOT", "ITS"}
        );
        studentDepartmentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentDepartmentSpinner.setAdapter(studentDepartmentSpinnerAdapter);

        RecyclerView recyclerView = findViewById(R.id.students);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            this.databaseHelper = new DatabaseHelper(this);
            this.databaseHelper.copyDatabase();

            SQLiteDatabase database = this.databaseHelper.openDatabase();
            if (database != null) {
                List<User> users = User.newQuery(this.databaseHelper).getAll();
                UserAdapter userAdapter = new UserAdapter(users);
                recyclerView.setAdapter(userAdapter);

                findViewById(R.id.add_student).setOnClickListener(v -> {
                    String name = ((EditText) findViewById(R.id.student_name)).getText().toString().trim();
                    String ageText = ((EditText) findViewById(R.id.student_age)).getText().toString().trim();
                    String department = (String) studentDepartmentSpinner.getSelectedItem();
                    String gpaText = ((EditText) findViewById(R.id.student_gpa)).getText().toString().trim();

                    if (name.isEmpty() || ageText.isEmpty() || gpaText.isEmpty()) {
                        Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int age;
                    double gpa;
                    try {
                        age = Integer.parseInt(ageText);
                        gpa = Double.parseDouble(gpaText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid age or GPA.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User newUser = User.newQuery(this.databaseHelper).create(name, age, department, gpa);
                    Toast.makeText(this, "Student added successfully!", Toast.LENGTH_SHORT).show();

                    users.add(newUser);
                    userAdapter.notifyItemInserted(users.size() - 1);

                    ((EditText) findViewById(R.id.student_name)).setText("");
                    ((EditText) findViewById(R.id.student_age)).setText("");
                    ((EditText) findViewById(R.id.student_gpa)).setText("");
                    studentDepartmentSpinner.setSelection(0);
                });

                findViewById(R.id.show_top_students).setOnClickListener(v -> {
                    try {
                        List<User> allUsers = User.newQuery(this.databaseHelper).getTop3();
                        userAdapter.updateData(allUsers);
                        userAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(userAdapter);
                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                findViewById(R.id.reset_fields).setOnClickListener(v -> {
                    ((EditText) findViewById(R.id.student_name)).setText("");
                    ((EditText) findViewById(R.id.student_age)).setText("");
                    ((EditText) findViewById(R.id.student_gpa)).setText("");
                    studentDepartmentSpinner.setSelection(0);
                    Toast.makeText(this, "Fields reset!", Toast.LENGTH_SHORT).show();
                });

                findViewById(R.id.show_all_users).setOnClickListener(v -> {
                    try {
                        List<User> allUsers = User.newQuery(this.databaseHelper).getAll();
                        userAdapter.updateData(allUsers);
                        userAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(userAdapter);
                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.databaseHelper = null;
    }
}
