package ft.yaeb.matcher;

public interface EventMatcher {
    void initialize(Class<?> eventType, String[] params);

    boolean matches(Object event);
}
