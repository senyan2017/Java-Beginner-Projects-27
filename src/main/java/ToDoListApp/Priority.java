package ToDoListApp;

/**
 * Task priority levels, ordered from most to least urgent.
 */
public enum Priority {
    HIGH,
    MEDIUM,
    LOW;

    /** The priority used when the user does not specify one. */
    public static final Priority DEFAULT = MEDIUM;

    /**
     * Parses a priority from user input, accepting any letter case and
     * surrounding whitespace.
     *
     * @throws IllegalArgumentException if the text does not match a known level
     */
    public static Priority fromString(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Priority cannot be null.");
        }
        String normalized = text.trim().toUpperCase();
        switch (normalized) {
            case "HIGH":
            case "H":
                return HIGH;
            case "MEDIUM":
            case "MED":
            case "M":
                return MEDIUM;
            case "LOW":
            case "L":
                return LOW;
            default:
                throw new IllegalArgumentException("Unknown priority: " + text);
        }
    }

    /**
     * Parses a priority, falling back to {@code fallback} when the text is
     * blank or invalid. Never throws.
     */
    public static Priority fromStringOrDefault(String text, Priority fallback) {
        if (text == null || text.trim().isEmpty()) {
            return fallback;
        }
        try {
            return fromString(text);
        } catch (IllegalArgumentException ex) {
            return fallback;
        }
    }
}
