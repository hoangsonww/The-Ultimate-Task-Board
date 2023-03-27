public class Feedback {

    private Task task;
    private int rating;
    private String comments;

    public Feedback(Task task, int rating, String comments) {
        this.task = task;
        this.rating = rating;
        this.comments = comments;
    }

    public Task getTask() {
        return task;
    }

    public int getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }
}
