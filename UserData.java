import java.time.LocalDateTime;
import java.util.Scanner;

public class UserData {
    private Task task;
    private LocalDateTime completionTime;
    private String notes;

    public UserData(Task task) {
        this.task = task;
        this.completionTime = LocalDateTime.now();
        this.notes = "";
    }

    public Task getTask() {
        return task;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void gatherData() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Task completed: " + task.getName());
        System.out.print("Notes (optional): ");
        String notes = scanner.nextLine();
        setNotes(notes);
    }
}
