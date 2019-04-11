package ft.yaeb;

import ft.yaeb.exception.EventRegistrationException;

public interface EventBus extends EventPublisher {
    void register(final Object eventListener) throws EventRegistrationException;
    void unRegister(final Object eventListener);
    void shutdown();
}
