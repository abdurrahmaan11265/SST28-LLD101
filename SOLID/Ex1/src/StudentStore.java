import java.util.List;

public interface StudentStore {
    void save(StudentRecord sr);
    int count();
    List<StudentRecord> all();
}
