package ft.yaeb.registry;

import ft.yaeb.exception.EventRegistrationException;

import java.util.Set;

public interface EventListenerRegistry {
    void register(Object eventListener) throws EventRegistrationException;

    void unRegister(Object eventListener);

    Set<EventListenerRecord> getEventListeners(Object event);

    void close();
}
