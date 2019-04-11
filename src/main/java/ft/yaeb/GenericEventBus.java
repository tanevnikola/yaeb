package ft.yaeb;

import ft.yaeb.dispatcher.EventDispatcher;
import ft.yaeb.exception.EventRegistrationException;
import ft.yaeb.registry.EventListenerRegistry;
import ft.yaeb.registry.GenericEventListenerRegistry;

public class GenericEventBus implements EventBus {
    private final EventListenerRegistry eventListenerRegistry = new GenericEventListenerRegistry();
    private final EventDispatcher eventDispatcher;

    public GenericEventBus(final EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public void register(final Object eventListener) throws EventRegistrationException {
        eventListenerRegistry.register(eventListener);
    }

    public void unRegister(final Object eventListener) {
        eventListenerRegistry.unRegister(eventListener);
    }

    @Override
    public final void publish(final Object event) {
        eventDispatcher.dispatch(eventListenerRegistry.getEventListeners(event), event);
    }

    @Override
    public void shutdown() {
        eventDispatcher.shutdown();
        eventListenerRegistry.close();
    }
}
