import java.time.Duration;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class TaskPredictor {
    private List<Task> tasks;

    public TaskPredictor(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void predictTaskCompletionTime(Task task) throws Exception {
        // Build the training dataset
        Instances dataset = buildDataset();

        // Build the classifier
        Classifier classifier = new NaiveBayes();
        classifier.buildClassifier(dataset);

        // Build the test instance
        Instance testInstance = buildTestInstance(task, dataset);

        // Make the prediction
        double prediction = classifier.classifyInstance(testInstance);

        // Display the prediction
        System.out.println("Predicted task completion time for " + task.getName() + ": " + prediction + " days");
    }

    private Instances buildDataset() {
        // Define the attributes
        Attribute priority = new Attribute("priority");
        Attribute duration = new Attribute("duration");
        Attribute notes = new Attribute("notes");
        List<String> statusValues = new ArrayList<>();
        statusValues.add("completed");
        statusValues.add("incomplete");
        Attribute status = new Attribute("status", statusValues);

        // Create the dataset
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(priority);
        attributes.add(duration);
        attributes.add(notes);
        attributes.add(status);
        Instances dataset = new Instances("TaskCompletionTimes", attributes, tasks.size());

        // Add the task instances to the dataset
        for (Task task : tasks) {
            double priorityValue = convertPriorityToValue(task.getPriority());
            double durationValue = convertDurationToValue(task.getDuration());
            Instance instance = new DenseInstance(4);
            instance.setValue(priority, priorityValue);
            instance.setValue(duration, durationValue);
            instance.setValue(notes, task.getNotes());
            instance.setValue(status, task.getStatus().toString());
            dataset.add(instance);
        }

        // Set the class index to the status attribute
        dataset.setClassIndex(3);

        return dataset;
    }

    private Instance buildTestInstance(Task task, Instances dataset) {
        // Create a new instance with the same attributes as the dataset
        Instance testInstance = new DenseInstance(dataset.numAttributes());
        testInstance.setDataset(dataset);

        // Set the attribute values for the test instance
        double priorityValue = convertPriorityToValue(task.getPriority());
        double durationValue = convertDurationToValue(task.getDuration());
        testInstance.setValue(0, priorityValue);
        testInstance.setValue(1, durationValue);
        testInstance.setValue(2, task.getNotes());

        return testInstance;
    }

    private double convertPriorityToValue(Priority priority) {
        switch (priority) {
            case HIGH:
                return 3.0;
            case MEDIUM:
                return 2.0;
            case LOW:
                return 1.0;
            default:
                return 0.0;
        }
    }


    private double convertDurationToValue(Duration duration) {
        // Convert the duration to seconds
        long seconds = (long) duration.getSeconds();

        // Convert seconds to a value between 0 and 1
        double value = (double) seconds / (30.0 * 24.0 * 60.0 * 60.0);

        return value;
    }

}
