package ft.eventpublisher.dispatcher;

import ft.eventpublisher.registry.EventListenerRecord;

import java.util.Set;

public interface EventDispatcher {
    void dispatch(Set<EventListenerRecord> eventListenerRecords, Object event);

    void shutdown();
}
