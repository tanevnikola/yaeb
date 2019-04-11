package ft.yaeb.dispatcher;

import ft.yaeb.registry.EventListenerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class ExecutorServiceEventDispatcher extends AbstractEventDispatcher implements DispatchFacilitator {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceEventDispatcher.class);

    // this will be our broker
    private final ExecutorService executorService;

    public ExecutorServiceEventDispatcher(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void dispatch(final EventListenerRecord eventListenerRecord, final Object event) {
        if (!executorService.isShutdown()) {
            executorService.submit(() -> {
                try {
                    eventListenerRecord.getEventHandler().invoke(eventListenerRecord.getEventListener(), event);
                } catch (Exception e) {
                    logger.warn("Exception while trying to dispatch event '" + event.getClass() + "'", e);
                }
            });
        }
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }
}
