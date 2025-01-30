package ivk.danilo.v6;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ivk.danilo.v6.Adapters.UserAdapter;
import ivk.danilo.v6.Models.Base.Attribute.Attribute;
import ivk.danilo.v6.Models.Base.Model;
import ivk.danilo.v6.Models.User;
import ivk.danilo.v6.Sqlite.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private List<Model> users = new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private EditText studentNameEditText = null;
    private EditText studentAgeEditText = null;
    private EditText studentGpaEditText = null;
    private Spinner studentDepartmentSpinner = null;
    private Button addStudentButton = null;
    private Button showAllStudentsButton = null;
    private Button showTopStudentsButton = null;
    private Button resetInputFieldsButton = null;
    private UserAdapter userAdapter = null;
    private RecyclerView usersRecyclerView = null;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        this.setContentView(R.layout.activity_main);

        this.initializeViews();

        ArrayAdapter<String> studentDepartmentSpinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"SRT", "KOT", "ITS"}
        );

        studentDepartmentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.studentDepartmentSpinner.setAdapter(studentDepartmentSpinnerAdapter);

        this.usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.userAdapter = new UserAdapter(this.users);

        this.usersRecyclerView.setAdapter(this.userAdapter);

        try {
            this.initializeComponents();

            this.databaseHelper = new DatabaseHelper(this);
            this.databaseHelper.copyDatabase();

            if (this.databaseHelper.openDatabase() == null) {
                Toast.makeText(this, R.string.error_opening_database, Toast.LENGTH_LONG).show();

                this.databaseHelper.close();

                return;
            }

            this.users = User.query(this.getFilesDir().getPath()).get();

            this.userAdapter.updateData(this.users);

            this.userAdapter.notifyDataSetChanged();
        } catch (Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.databaseHelper = null;
    }

    private void initializeComponents() {
        this.initializeAddStudentButton();
        this.initializeShowAllStudentsButton();
        this.initializeTopStudentsButton();
        this.initializeResetInputFieldsButton();
    }

    private void initializeViews() {
        this.studentNameEditText = findViewById(R.id.student_name);
        this.studentAgeEditText = findViewById(R.id.student_age);
        this.studentGpaEditText = findViewById(R.id.student_gpa);
        this.studentDepartmentSpinner = findViewById(R.id.student_department);

        this.addStudentButton = findViewById(R.id.add_student);
        this.showAllStudentsButton = findViewById(R.id.show_all_users);
        this.showTopStudentsButton = findViewById(R.id.show_top_students);
        this.resetInputFieldsButton = findViewById(R.id.reset_fields);

        this.usersRecyclerView = findViewById(R.id.students);
    }

    private void initializeAddStudentButton() {
        this.addStudentButton.setOnClickListener(v -> {
            String name = this.studentNameEditText.getEditableText().toString().trim();
            String ageText = this.studentAgeEditText.getEditableText().toString().trim();
            String department = this.studentDepartmentSpinner.getSelectedItem().toString().trim();
            String gpaText = this.studentGpaEditText.getEditableText().toString().trim();

            if (name.isEmpty() || ageText.isEmpty() || gpaText.isEmpty()) {
                Toast.makeText(this, R.string.all_fields_required, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int age = Integer.parseInt(ageText);
                double gpa = Double.parseDouble(gpaText);

                Model newUser = User.query(this.getFilesDir().getPath()).create(
                        new Attribute("name", name),
                        new Attribute("age", age),
                        new Attribute("department", department),
                        new Attribute("gpa", gpa)
                );

                Toast.makeText(this, R.string.student_added_successfully, Toast.LENGTH_SHORT).show();

                this.users.add(newUser);
                userAdapter.notifyItemInserted(this.users.size() - 1);
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.age_or_gpa_not_a_number, Toast.LENGTH_SHORT).show();
            } finally {
                this.resetInputFields();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeShowAllStudentsButton() {
        this.showAllStudentsButton.setOnClickListener(v -> {
            try {
                List<Model> allUsers = User.query(this.getFilesDir().getPath())
                                           .get();

                this.userAdapter.updateData(allUsers);
                this.userAdapter.notifyDataSetChanged();
                this.usersRecyclerView.setAdapter(this.userAdapter);
            } catch (Exception exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeTopStudentsButton() {
        this.showTopStudentsButton.setOnClickListener(v -> {
            try {
                List<Model> topStudents = User.query(this.getFilesDir().getPath())
                                              .orderByDesc("gpa")
                                              .limit(3)
                                              .get();

                this.userAdapter.updateData(topStudents);
                this.userAdapter.notifyDataSetChanged();
                this.usersRecyclerView.setAdapter(this.userAdapter);
            } catch (Exception exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeResetInputFieldsButton() {
        this.resetInputFieldsButton.setOnClickListener(v -> {
            this.resetInputFields();

            Toast.makeText(this, R.string.fields_reset, Toast.LENGTH_SHORT).show();
        });
    }

    private void resetInputFields() {
        this.studentNameEditText.setText("");
        this.studentAgeEditText.setText("");
        this.studentGpaEditText.setText("");
        this.studentDepartmentSpinner.setSelection(0);
    }
}
