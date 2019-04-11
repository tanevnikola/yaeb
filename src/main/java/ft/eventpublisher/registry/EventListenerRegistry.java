package ft.eventpublisher.registry;

import ft.eventpublisher.exception.EventRegistrationException;

import java.util.Set;

public interface EventListenerRegistry {
    void register(Object eventListener) throws EventRegistrationException;

    void unRegister(Object eventListener);

    Set<EventListenerRecord> getEventListeners(Object event);

    void close();
}
