import java.util.Date;

public class Reminder {
    private Date date;
    private String message;

    public Reminder(Date date, String message) {
        this.date = date;
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
}