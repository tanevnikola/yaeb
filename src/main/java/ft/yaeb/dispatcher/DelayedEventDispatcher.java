package ft.yaeb.dispatcher;

import ft.yaeb.registry.EventListenerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DelayedEventDispatcher extends AbstractFacilitatedEventDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(DelayedEventDispatcher.class);

    private static final String DELAYED_EVENT_THREAD_NAME = "delayedEventThread";
    private static final String DELAYED_EVENT_THREAD_GROUP_NAME = AbstractEventDispatcher.class.toString();

    private final Thread delayedEventThread;
    private final AtomicBoolean delayedEventThreadRunning = new AtomicBoolean(true);
    private final DelayQueue<DelayedEventRecord> delayedEventRecords = new DelayQueue<>();
    private final long delayMS;

    public DelayedEventDispatcher(final DispatchFacilitator dispatchFacilitator ,final long delayedEventPollTimeMS, final long delayMS) {
        super(dispatchFacilitator);

        this.delayMS = delayMS;
        delayedEventThread = new Thread(new ThreadGroup(DELAYED_EVENT_THREAD_GROUP_NAME), () -> {
            logger.trace("thread started");
            while (delayedEventThreadRunning.get()) {
                try {
                    Optional.ofNullable(delayedEventRecords.poll(delayedEventPollTimeMS, TimeUnit.MILLISECONDS))
                            .ifPresent(delayedEventRecord ->
                                    facilitateDispatch(delayedEventRecord.eventListenerRecord, delayedEventRecord.event));
                } catch (InterruptedException ignored) {}
            }
            logger.trace("thread ended");
        });
        delayedEventThread.setName(DELAYED_EVENT_THREAD_NAME);
        delayedEventThread.start();
    }

    @Override
    protected void dispatch(final EventListenerRecord eventListenerRecord, final Object event) {
        DelayedEventRecord delayedEventRecord = new DelayedEventRecord(eventListenerRecord, event);
        synchronized (delayedEventRecords) {
            if (!delayedEventRecords.contains(delayedEventRecord)) {
                delayedEventRecords.put(delayedEventRecord);
            }
        }
    }

    private class DelayedEventRecord implements Delayed {
        final EventListenerRecord eventListenerRecord;
        final Object event;
        final long timeStartedMS;

        DelayedEventRecord(final EventListenerRecord eventListenerRecord, final Object event) {
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
            return o instanceof DelayedEventRecord
                    && event.equals(((DelayedEventRecord) o).event)
                    && eventListenerRecord.equals(((DelayedEventRecord) o).eventListenerRecord);
        }

        @Override
        public long getDelay(final TimeUnit unit) {
            long timeLeftMS = timeStartedMS + delayMS - System.currentTimeMillis();
            return unit.convert(timeLeftMS, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(final Delayed o) {
            return (int)(getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }
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
}
