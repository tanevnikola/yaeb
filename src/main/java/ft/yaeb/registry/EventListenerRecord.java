package ft.yaeb.registry;

import ft.yaeb.annotation.Delayed;
import ft.yaeb.matcher.EventMatcher;

import java.lang.reflect.Method;

public class EventListenerRecord {
    private final Object eventListener;
    private final Method eventHandler;
    private final EventMatcher matcher;
    private final Delayed delayed;

    public EventListenerRecord(final Object eventListener, final Method eventHandler, final EventMatcher matcher, final Delayed delayed) {
        this.eventListener = eventListener;
        this.eventHandler = eventHandler;
        this.matcher = matcher;
        this.delayed = delayed;
    }

    public final Object getEventListener() {
        return eventListener;
    }

    public final Method getEventHandler() {
        return eventHandler;
    }

    public final EventMatcher getMatcher() {
        return matcher;
    }

    public final Delayed getDelayed() {
        return delayed;
    }
}