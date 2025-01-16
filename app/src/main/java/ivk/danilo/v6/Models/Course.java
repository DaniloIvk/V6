package ivk.danilo.v6.Models;

import java.util.Date;

public final class Course {
    public final int id;
    public final int studentId;
    public final String name;
    public final String createdAt;
    public final String updatedAt;

    public Course(int id, int studentId, String name, String createdAt, String updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Course(int id, int studentId, String name, String createdAt) {
        this(id, studentId, name, createdAt, createdAt);
    }

    public Course(int studentId, String name) {
        this(-1, studentId, name, new Date().toString());
    }
}
