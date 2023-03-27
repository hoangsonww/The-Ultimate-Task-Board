import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class RecommendationAlgorithm implements Serializable {
    private List<Task> tasks;
    private UserPreferences userPreferences;

    public RecommendationAlgorithm(List<Task> tasks, UserPreferences userPreferences) {
        this.tasks = tasks;
        this.userPreferences = userPreferences;
    }

    public Task recommendTask() {
        List<Task> incompletedTasks = getIncompletedTasks();

        // Sort incompleted tasks by priority and days since last completion
        incompletedTasks.sort((task1, task2) -> {
            double task1Priority = convertPriorityToValue(task1.getPriority());
            double task2Priority = convertPriorityToValue(task2.getPriority());
            int result = Double.compare(task2Priority, task1Priority);
            if (result == 0) {
                int daysSinceLastCompletion1 = getDaysSinceLastCompletion(task1);
                int daysSinceLastCompletion2 = getDaysSinceLastCompletion(task2);
                result = Integer.compare(daysSinceLastCompletion1, daysSinceLastCompletion2);
            }
            return result;
        });

        // Return the first task in the sorted list
        return incompletedTasks.get(0);
    }

    private List<Task> getIncompletedTasks() {
        // Get all tasks that have not been completed yet
        return tasks.stream()
                .filter(task -> task.getStatus() != Status.COMPLETED)
                .collect(Collectors.toList());
    }

    private int getDaysSinceLastCompletion(Task task) {
        LocalDate lastCompleted = task.getCompletedDate();
        if (lastCompleted == null) {
            // The task has never been completed, so return a large number
            return Integer.MAX_VALUE;
        } else {
            // Calculate the number of days between today and the last completion date
            LocalDate today = LocalDate.now();
            long days = ChronoUnit.DAYS.between(lastCompleted, today);
            return (int) days;
        }
    }

    private double convertPriorityToValue(Priority priority) {
        double value;
        switch (priority) {
            case HIGH:
                value = 1.0;
                break;
            case MEDIUM:
                value = 0.5;
                break;
            case LOW:
            default:
                value = 0.0;
                break;
        }
        return value;
    }
}
