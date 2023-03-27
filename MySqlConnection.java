import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class MySqlConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mysql2?user=root&password=Hoangson09112004@";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Hoangson09112004@";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static List<Task> getAllTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();

        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        String query = "SELECT * FROM tasks";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Status status = Status.valueOf(resultSet.getString("status"));
                Priority priority = Priority.valueOf(resultSet.getString("priority"));
                LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();
                Duration duration = Duration.ofMinutes(resultSet.getLong("duration"));
                String notes = resultSet.getString("notes");

                Task task = new Task(id, name, description, status, priority, dueDate, duration, notes);
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        connection.close();

        return tasks;
    }


    public static void addTask(Task task) throws SQLException {
        Connection connection = getConnection();

        String query = "INSERT INTO tasks (name, description, status, priority, due_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, task.getName());
        statement.setString(2, task.getDescription());
        statement.setString(3, task.getStatus().toString());
        statement.setString(4, task.getPriority().toString());
        statement.setDate(5, Date.valueOf(task.getDueDate()));

        statement.executeUpdate();

        closeConnection(connection, statement);
    }

    public static void updateTask(Task task) throws SQLException {
        Connection connection = getConnection();

        String query = "UPDATE tasks SET name = ?, description = ?, status = ?, priority = ?, due_date = ? " +
                "WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, task.getName());
        statement.setString(2, task.getDescription());
        statement.setString(3, task.getStatus().toString());
        statement.setString(4, task.getPriority().toString());
        statement.setDate(5, Date.valueOf(task.getDueDate()));
        statement.setInt(6, task.getId());

        statement.executeUpdate();

        closeConnection(connection, statement);
    }


    public static void deleteTask(int taskId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE id=?")) {
            stmt.setInt(1, taskId);
            stmt.executeUpdate();
        }
    }

    private static void closeConnection(Connection connection, PreparedStatement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

}
