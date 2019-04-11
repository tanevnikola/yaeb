package ft.eventpublisher;

import ft.eventpublisher.dispatcher.ExecutorServiceEventDispatcher;
import ft.eventpublisher.registry.GenericEventListenerRegistry;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class GenericEventPublisher extends AbstractEventPublisher {
    private static final String GROUP_NAME_PREFIX = "GENERIC_EVENT_PUBLISHER_GROUP_";
    private static final String THREAD_NAME_PREFIX = "GENERIC_EVENT_PUBLISHER_THREAD_";

    public GenericEventPublisher(final int numThreads) {
        super(
                new GenericEventListenerRegistry(),
                new ExecutorServiceEventDispatcher(Executors.newFixedThreadPool(
                        numThreads,
                        new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                Thread t = new Thread(new ThreadGroup(GROUP_NAME_PREFIX + hashCode()), r);
                                t.setName(THREAD_NAME_PREFIX + t.hashCode());
                                return t;
                            }
                        }))
        );
    }
}
