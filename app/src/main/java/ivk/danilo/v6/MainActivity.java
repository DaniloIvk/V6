package ivk.danilo.v6;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import ivk.danilo.v6.Adapters.UserAdapter;
import ivk.danilo.v6.Models.Base.Attribute.Attribute;
import ivk.danilo.v6.Models.Base.Model;
import ivk.danilo.v6.Models.Base.Utils;
import ivk.danilo.v6.Models.User;

public class MainActivity extends AppCompatActivity {
    private String databasePath = null;
    private String sortByColumn = User.getPrimaryKey();
    private String sortByDirection = "ASC";
    private EditText studentNameEditText = null;
    private EditText studentAgeEditText = null;
    private EditText studentGpaEditText = null;
    private Spinner studentDepartmentSpinner = null;
    private Spinner sortBySpinner = null;
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

        this.databasePath = this.getFilesDir().getPath();

        this.initializeViews();

        try {
            this.usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            List<Model> users = User.query(this.databasePath)
                                    .get();

            this.userAdapter = new UserAdapter(users);

            this.usersRecyclerView.setAdapter(this.userAdapter);

            this.initializeComponents();
        } catch (Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeComponents() {
        this.initializeAddStudentButton();
        this.initializeShowAllStudentsButton();
        this.initializeTopStudentsButton();
        this.initializeResetInputFieldsButton();

        this.initializeStudentDepartmentSpinner();
        this.initializeSortBySpinner();
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

        this.sortBySpinner = findViewById(R.id.sort_by);
        this.usersRecyclerView = findViewById(R.id.students);
    }

    @SuppressLint("CheckResult")
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

                User.query(this.databasePath)
                    .create(
                            new Attribute("name", name),
                            new Attribute("age", age),
                            new Attribute("department", department),
                            new Attribute("gpa", gpa)
                    );

                Toast.makeText(this, R.string.student_added_successfully, Toast.LENGTH_SHORT).show();

                this.updateUserList(null);
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
                this.updateUserList(null);
            } catch (Exception exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeTopStudentsButton() {
        this.showTopStudentsButton.setOnClickListener(v -> {
            try {
                List<Model> topStudents = User.query(this.databasePath)
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

    private void initializeStudentDepartmentSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"SRT", "KOT", "ITS"}
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.studentDepartmentSpinner.setAdapter(adapter);
    }

    private void initializeSortBySpinner() {
        String[] options = Utils.createSortableOptions("Name", "Age", "GPA", "Department");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.sortBySpinner.setAdapter(adapter);

        this.sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String option = options[position];

                if (Utils.isDefaultSortableOption(option)) {
                    sortByColumn = User.getPrimaryKey();
                    sortByDirection = "ASC";
                } else {
                    sortByColumn = Utils.getSortableColumn(option);
                    sortByDirection = Utils.getSortableOrder(option);
                }

                updateUserList(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void resetInputFields() {
        this.studentNameEditText.setText("");
        this.studentAgeEditText.setText("");
        this.studentGpaEditText.setText("");
        this.studentDepartmentSpinner.setSelection(0);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Contract(pure = true)
    private void updateUserList(@Nullable List<Model> users) {
        if (users == null) {
            users = User.query(this.databasePath)
                        .orderBy(this.sortByColumn, this.sortByDirection)
                        .get();
        }

        this.userAdapter.updateData(users);
        this.userAdapter.notifyDataSetChanged();
    }
}
