import java.util.HashMap;
import java.util.Map;

public class UserPreferences {
    private Map<String, Object> preferences;

    public UserPreferences() {
        preferences = new HashMap<>();
    }

    public void setPreference(String key, Object value) {
        preferences.put(key, value);
    }

    public Object getPreference(String key) {
        return preferences.get(key);
    }
}
