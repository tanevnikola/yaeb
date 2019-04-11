package ft.eventpublisher;

import ft.eventpublisher.dispatcher.EventDispatcher;
import ft.eventpublisher.exception.EventRegistrationException;
import ft.eventpublisher.registry.EventListenerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractEventPublisher implements EventPublisher {
    private static Logger log = LoggerFactory.getLogger(AbstractEventPublisher.class);

    private final EventListenerRegistry eventListenerRegistry;
    private final EventDispatcher eventDispatcher;

    public AbstractEventPublisher(final EventListenerRegistry eventListenerRegistry, final EventDispatcher eventDispatcher) {
        this.eventListenerRegistry = eventListenerRegistry;
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

        log.trace("Shutdown complete!");
    }
}
