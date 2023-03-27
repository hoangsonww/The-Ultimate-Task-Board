import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TaskManager {
    private Map<Task, LocalDateTime> completionTimes;
    private Map<Task, String> notes;

    public TaskManager() {
        this.completionTimes = new HashMap<>();
        this.notes = new HashMap<>();
    }

    public void addTask(Task task) {
        completionTimes.put(task, null);
        notes.put(task, "");
    }

    public void completeTask(Task task) {
        // Update the completion time for the task
        LocalDateTime completionTime = LocalDateTime.now();
        completionTimes.put(task, completionTime);

        // Gather notes from the user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter notes for the completed task (press enter to skip): ");
        String userNotes = scanner.nextLine();
        notes.put(task, userNotes);
    }

    public LocalDateTime getCompletionTime(Task task) {
        return completionTimes.get(task);
    }

    public String getNotes(Task task) {
        return notes.get(task);
    }
}
