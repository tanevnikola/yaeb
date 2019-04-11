package ft.eventpublisher.matcher;

public abstract class AbstractEventMatcher implements EventMatcher {
    @Override
    public final boolean matches(Object event) {
        return getEventType().isAssignableFrom(event.getClass()) && matchesImpl(event);
    }

    public abstract Class<?> getEventType();

    protected abstract boolean matchesImpl(Object event);
}
