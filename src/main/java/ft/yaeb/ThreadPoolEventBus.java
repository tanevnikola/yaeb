package ft.yaeb;

import ft.yaeb.dispatcher.ExecutorServiceEventDispatcher;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadPoolEventBus extends GenericEventBus {
    private static final String GROUP_NAME_PREFIX = ThreadPoolEventBus.class.getCanonicalName();
    private static final String THREAD_NAME_PREFIX = ThreadPoolEventBus.class.getCanonicalName();

    public ThreadPoolEventBus(final int numThreads) {
        super(
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
