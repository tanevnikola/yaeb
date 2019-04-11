package ft.eventpublisher.dispatcher;

import ft.eventpublisher.registry.EventListenerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractEventDispatcher implements EventDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(AbstractEventDispatcher.class);

    private static final String DELAYED_EVENT_THREAD_NAME = "delayedEventThread";
    private static final String DELAYED_EVENT_THREAD_GROUP_NAME = AbstractEventDispatcher.class.toString();

    private final Thread delayedEventThread;
    private final AtomicBoolean delayedEventThreadRunning = new AtomicBoolean(true);
    private final DelayQueue<DelayedEventDispatcher> delayedEventDispatchers = new DelayQueue<>();

    public AbstractEventDispatcher(final long delayedEventPollTimeMS) {
        delayedEventThread = new Thread(new ThreadGroup(DELAYED_EVENT_THREAD_GROUP_NAME), () -> {
            logger.trace("thread started");
            while (delayedEventThreadRunning.get()) {
                try {
                    Optional.ofNullable(delayedEventDispatchers.poll(delayedEventPollTimeMS, TimeUnit.MILLISECONDS))
                            .ifPresent(DelayedEventDispatcher::dispatch);
                } catch (InterruptedException ignored) {
                    // IGNORED
                }
            }
            logger.trace("thread ended");
        });
        delayedEventThread.setName(DELAYED_EVENT_THREAD_NAME);
        delayedEventThread.start();
    }

    @Override
    public final void dispatch(final Set<EventListenerRecord> eventListenerRecords, final Object event) {
        Objects.requireNonNull(eventListenerRecords);
        Objects.requireNonNull(event);

        eventListenerRecords.forEach(eventRecord -> {
            if (eventRecord.getDelayed().value() == 0) {
                dispatch(eventRecord, event);
            } else {
                DelayedEventDispatcher delayedEventDispatcher = new DelayedEventDispatcher(eventRecord, event);
                synchronized (delayedEventDispatchers) {
                    if (!delayedEventDispatchers.contains(delayedEventDispatcher)) {
                        delayedEventDispatchers.put(delayedEventDispatcher);
                    }
                }
            }
        });
    }

    @Override
    public void shutdown() {
        delayedEventThreadRunning.set(false);

        synchronized (delayedEventThread) {
            try {
                delayedEventThread.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        logger.trace("Closed!");
    }

    protected abstract void dispatch(EventListenerRecord eventListenerRecord, Object event);

    private class DelayedEventDispatcher implements Delayed {
        final EventListenerRecord eventListenerRecord;
        final Object event;
        final long timeStartedMS;

        DelayedEventDispatcher(final EventListenerRecord eventListenerRecord, final Object event) {
            this.eventListenerRecord = eventListenerRecord;
            this.event = event;
            this.timeStartedMS = System.currentTimeMillis();
        }

        @Override
        public int hashCode() {
            return event.hashCode() + 31 * eventListenerRecord.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            return o instanceof DelayedEventDispatcher
                    && event.equals(((DelayedEventDispatcher) o).event)
                    && eventListenerRecord.equals(((DelayedEventDispatcher) o).eventListenerRecord);
        }

        @Override
        public long getDelay(final TimeUnit unit) {
            long delayMS = TimeUnit.MILLISECONDS.convert(
                    eventListenerRecord.getDelayed().value(),
                    eventListenerRecord.getDelayed().timeUnit());
            long timeLeftMS = timeStartedMS + delayMS - System.currentTimeMillis();
            return unit.convert(timeLeftMS, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(final Delayed o) {
            return (int)(getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }

        void dispatch() {
            AbstractEventDispatcher.this.dispatch(eventListenerRecord, event);
        }
    }
}
