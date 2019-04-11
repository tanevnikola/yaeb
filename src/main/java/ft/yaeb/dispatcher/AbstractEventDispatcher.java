package ft.yaeb.dispatcher;

import ft.yaeb.registry.EventListenerRecord;

import java.util.Objects;
import java.util.Set;

public abstract class AbstractEventDispatcher implements EventDispatcher {
    @Override
    public final void dispatch(final Set<EventListenerRecord> eventListenerRecords, final Object event) {
        Objects.requireNonNull(eventListenerRecords);
        Objects.requireNonNull(event);

        eventListenerRecords.forEach(eventRecord -> dispatch(eventRecord, event));
    }

    protected abstract void dispatch(EventListenerRecord eventListenerRecord, Object event);
}
