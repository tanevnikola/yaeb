package ft.eventpublisher.dispatcher;

import ft.eventpublisher.registry.EventListenerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class ExecutorServiceEventDispatcher extends AbstractEventDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceEventDispatcher.class);

    private static final long DELAYED_EVENT_DEFAULT_POLL_TIME_MS = 25;

    // this will be our broker
    private final ExecutorService executorService;

    public ExecutorServiceEventDispatcher(final ExecutorService executorService) {
        super(DELAYED_EVENT_DEFAULT_POLL_TIME_MS);
        this.executorService = executorService;
    }

    public ExecutorServiceEventDispatcher(final ExecutorService executorService, final long delayedEventPollTimeMS) {
        super(delayedEventPollTimeMS);
        this.executorService = executorService;
    }

    @Override
    protected void dispatch(final EventListenerRecord eventListenerRecord, final Object event) {
        if (!executorService.isShutdown()) {
            executorService.submit(() -> {
                try {
                    eventListenerRecord.getEventHandler().invoke(eventListenerRecord.getEventListener(), event);
                } catch (Exception e) {
                    logger.warn("Exception while trying to publish event '" + event.getClass() + "'", e);
                }
            });
        }
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
        super.shutdown();
    }
}
