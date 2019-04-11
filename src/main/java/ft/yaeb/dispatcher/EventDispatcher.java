package ft.yaeb.dispatcher;

import ft.yaeb.registry.EventListenerRecord;

import java.util.Set;

public interface EventDispatcher {
    void dispatch(Set<EventListenerRecord> eventListenerRecords, Object event);

    void shutdown();
}
