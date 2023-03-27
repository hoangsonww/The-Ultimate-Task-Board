import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import java.time.Duration;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class TaskRecommendationSystem {
    private List<Task> tasks;
    private Map<String, Integer> taskNameToIdMap;

    private UserPreferences userPreferences;

    public TaskRecommendationSystem(List<Task> tasks) {
        this.tasks = tasks;
        this.taskNameToIdMap = tasks.stream().collect(Collectors.toMap(Task::getName, Task::getId));
        this.userPreferences = new UserPreferences();
    }

    public List<Task> recommendTasks(Task currentTask) throws Exception {
        // Build the training dataset
        Instances dataset = buildDataset();

        // Build the classifier
        J48 classifier = new J48();
        classifier.setUnpruned(true);
        classifier.buildClassifier(dataset);

        // Build the test instance
        Instance testInstance = buildTestInstance(currentTask, dataset);

        // Get the predicted class value
        double predictedClass = classifier.classifyInstance(testInstance);

        // Get the recommended task names
        List<String> recommendedTaskNames = getRecommendedTaskNames(predictedClass);

        // Map the task names to task objects
        return recommendedTaskNames.stream().map(name -> tasks.get(taskNameToIdMap.get(name))).collect(Collectors.toList());
    }

    private Instances buildDataset() {
        // Define the attributes
        Attribute priority = new Attribute("priority");
        Attribute duration = new Attribute("duration");
        Attribute notes = new Attribute("notes");
        Attribute daysSinceLastCompletion = new Attribute("days_since_last_completion");
        List<String> statusValues = new ArrayList<>();
        statusValues.add("completed");
        statusValues.add("incomplete");
        Attribute status = new Attribute("status", statusValues);

        // Create the dataset
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(priority);
        attributes.add(duration);
        attributes.add(notes);
        attributes.add(daysSinceLastCompletion);
        attributes.add(status);
        Instances dataset = new Instances("TaskRecommendations", attributes, tasks.size());

        // Add the task instances to the dataset
        for (Task task : tasks) {
            double priorityValue = convertPriorityToValue(task.getPriority());
            double durationValue = convertDurationToValue(task.getDuration());
            double daysSinceLastCompletionValue = convertDaysSinceLastCompletionToValue(task.getDaysSinceLastCompletion());
            Instance instance = new DenseInstance(5);
            instance.setValue(priority, priorityValue);
            instance.setValue(duration, durationValue);
            instance.setValue(notes, task.getNotes());
            instance.setValue(daysSinceLastCompletion, daysSinceLastCompletionValue);
            instance.setValue(status, task.getStatus().toString());
            dataset.add(instance);
        }

        // Set the class index to the status attribute
        dataset.setClassIndex(4);

        return dataset;
    }


    private Instance buildTestInstance(Task currentTask, Instances dataset) {
        Instance testInstance = new DenseInstance(5);
        double priorityValue = convertPriorityToValue(currentTask.getPriority());
        double durationValue = convertDurationToValue(currentTask.getDuration());
        double daysSinceLastCompletionValue = convertDaysSinceLastCompletionToValue(currentTask.getDaysSinceLastCompletion());
        testInstance.setValue(dataset.attribute(0), priorityValue);
        testInstance.setValue(dataset.attribute(1), durationValue);
        testInstance.setValue(dataset.attribute(2), currentTask.getNotes());
        testInstance.setValue(dataset.attribute(3), daysSinceLastCompletionValue);
        testInstance.setDataset(dataset);
        return testInstance;
    }


    private List<String> getRecommendedTaskNames(double predictedClass) {
        List<String> recommendedTaskNames = new ArrayList<>();

        Task recommendedTask = null;
        // If the predicted class is "completed", recommend the highest-priority incomplete task
        if (predictedClass == 0) {
            List<Task> incompleteTasks = tasks.stream()
                    .filter(task -> task.getStatus().equals(Status.INCOMPLETE))
                    .sorted((t1, t2) -> t2.getPriority().compareTo(t1.getPriority()))
                    .collect(Collectors.toList());
            if (!incompleteTasks.isEmpty()) {
                recommendedTaskNames.add(incompleteTasks.get(0).getName());
            }
        }

        // If the predicted class is "incomplete", recommend the highest-priority task
        if (predictedClass == 1) {
            List<Task> sortedTasks = tasks.stream()
                    .sorted((t1, t2) -> t2.getPriority().compareTo(t1.getPriority()))
                    .collect(Collectors.toList());
            if (!sortedTasks.isEmpty()) {
                recommendedTaskNames.add(sortedTasks.get(0).getName());
            }
        }

        Feedback feedback = getUserFeedback(recommendedTask);
        adjustRecommendationAlgorithm(feedback);
        return recommendedTaskNames;
    }

    private Feedback getUserFeedback(Task recommendedTask) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("The recommended task is: " + recommendedTask.getName());
        System.out.println("How would you rate this task? (1-10)");
        int rating = scanner.nextInt();
        System.out.println("Would you like to add any comments? (y/n)");
        String comments = "";
        if (scanner.next().equalsIgnoreCase("y")) {
            System.out.println("Please enter your comments:");
            comments = scanner.next();
        }
        Feedback feedback = new Feedback(recommendedTask, rating, comments);
        saveFeedback(feedback);
        return feedback;
    }

    private double calculateScore(Task task) {
        double score = 0;
        double priorityValue = convertPriorityToValue(task.getPriority());
        double durationValue = convertDurationToValue(task.getDuration());
        double daysSinceCompletion = getDaysSinceLastCompletion(task);

        score += priorityValue;
        score += durationValue;
        score += daysSinceCompletion;

        return score;
    }

    private void adjustRecommendationAlgorithm(Feedback feedback) {
        // You can use the feedback to adjust the recommendation algorithm
        // For example, you could increase the score of tasks with similar characteristics to the recommended task
        // Or you could decrease the score of tasks with characteristics that the user did not like about the recommended task

        // For now, let's just print the feedback to the console
        System.out.println("User feedback:");
        System.out.println("Task name: " + feedback.getTask().getName());
        System.out.println("Rating: " + feedback.getRating());
        System.out.println("Comments: " + feedback.getComments());
    }

    private void saveFeedback(Feedback feedback) {
        System.out.println("Saving feedback:");
        System.out.println("Task name: " + feedback.getTask().getName());
        System.out.println("Rating: " + feedback.getRating());
        System.out.println("Comments: " + feedback.getComments());
    }

    public int getDaysSinceLastCompletion(Task task) {
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

    public void saveRecommendationAlgorithm(RecommendationAlgorithm algorithm) {
        // Save the algorithm to a file
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("algorithm.ser"))) {
            outputStream.writeObject(algorithm);
            System.out.println("Algorithm saved successfully");
        } catch (IOException e) {
            System.err.println("Error saving algorithm: " + e.getMessage());
        }
    }

    private double convertPriorityToValue(Priority priority) {
        switch (priority) {
            case HIGH:
                return 1.0;
            case MEDIUM:
                return 0.5;
            case LOW:
                return 0.0;
            default:
                return 0.0;
        }
    }

    private double convertDurationToValue(Duration duration) {
        long seconds = duration.getSeconds();
        return (double) seconds / (60 * 60); // Convert to hours
    }


    private double convertDaysSinceLastCompletionToValue(int daysSinceLastCompletion) {
        return (double) daysSinceLastCompletion / 30.0;
    }
}
