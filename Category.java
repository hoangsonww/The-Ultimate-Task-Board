import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private List<Task> tasks;

    public Category(String name) {
        this.name = name;
        this.tasks = new ArrayList<Task>();
    }

    public String getName() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }
}