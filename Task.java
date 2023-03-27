import java.time.Duration;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;
    private Priority priority;
    private LocalDate dueDate;

    private Duration duration;
    private String notes;
    private LocalDate completedDate;

    private LocalDate lastCompletionDate;

    public Task(int id, String name, String description, Status status, Priority priority, LocalDate dueDate, Duration duration, String notes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.duration = duration;
        this.notes = notes;
        this.lastCompletionDate = null;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getNotes() {
        return notes;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public int getDaysSinceLastCompletion() {
        if (lastCompletionDate == null) {
            return Integer.MAX_VALUE;
        } else {
            return (int) ChronoUnit.DAYS.between(lastCompletionDate, LocalDate.now());
        }
    }

    public void setLastCompletionDate(LocalDate date) {
        this.lastCompletionDate = date;
    }

}
