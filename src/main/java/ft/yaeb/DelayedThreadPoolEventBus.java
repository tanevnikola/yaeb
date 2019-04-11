package ft.yaeb;

import ft.yaeb.dispatcher.DelayedEventDispatcher;
import ft.yaeb.dispatcher.DispatchFacilitator;
import ft.yaeb.dispatcher.ExecutorServiceEventDispatcher;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class DelayedThreadPoolEventBus extends GenericEventBus {
    private static final long DEFAULT_DELAYED_EVENT_POLL_TIME_MS = 25;
    private static final String GROUP_NAME_PREFIX = ThreadPoolEventBus.class.getCanonicalName();
    private static final String THREAD_NAME_PREFIX = ThreadPoolEventBus.class.getCanonicalName();

    public DelayedThreadPoolEventBus(final DispatchFacilitator dispatchFacilitator, final long delayTimeMS) {

        super(new DelayedEventDispatcher(new ExecutorServiceEventDispatcher(Executors.newFixedThreadPool(
                0,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(new ThreadGroup(GROUP_NAME_PREFIX + hashCode()), r);
                        t.setName(THREAD_NAME_PREFIX + t.hashCode());
                        return t;
                    }
                })), DEFAULT_DELAYED_EVENT_POLL_TIME_MS, delayTimeMS));
    }
}
