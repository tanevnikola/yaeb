package ft.eventpublisher.registry;

import ft.eventpublisher.annotation.Subscribe;
import ft.eventpublisher.exception.EventRegistrationException;
import ft.eventpublisher.matcher.EventMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * Event listener registry that uses {@code {@link CopyOnWriteArraySet}} to store the event listeners. This
 * consequently means that register/unregister operations are expensive, however it provides safer management
 * of the listeners.
 */
public final class GenericEventListenerRegistry implements EventListenerRegistry {
    private static final Logger logger = LoggerFactory.getLogger(GenericEventListenerRegistry.class);
    private static final String EVENT_MATCHER_INITIALIZER = "initialize";
    private static final int EVENT_HANDLER_METHOD_NUM_PARAMETERS = 1;
    private static final int EVENT_HANDLER_METHOD_EVENT_TYPE_PARAMETER_INDEX = 0;

    private final Set<EventListenerRecord> eventListenerRecords = new CopyOnWriteArraySet<>();

    @Override
    public void register(final Object eventListener) throws EventRegistrationException {
        Objects.requireNonNull(eventListener);

        for (Method method : eventListener.getClass().getMethods()) {
            if (Objects.nonNull(method.getAnnotation(Subscribe.class))) {
                registerEventHandler(eventListener, method);
            }
        }
    }

    @Override
    public void unRegister(final Object eventListener) {
        Objects.requireNonNull(eventListener);

        eventListenerRecords.removeIf(record -> record.getEventListener() == eventListener);
    }

    @Override
    public Set<EventListenerRecord> getEventListeners(final Object event) {
        Objects.requireNonNull(event);

        return eventListenerRecords.stream()
                .filter(e -> e.getMatcher().matches(event))
                .collect(Collectors.toSet());
    }

    @Override
    public void close() {
        eventListenerRecords.clear();
        logger.trace("Closed!");
    }

    private void registerEventHandler(final Object eventListener, final Method eventHandler) throws EventRegistrationException {
        if (eventHandler.getParameterCount() != EVENT_HANDLER_METHOD_NUM_PARAMETERS) {
            throw new EventRegistrationException("Cannot subscribe event handler, " + eventListener.getClass().getCanonicalName() + ":" + eventHandler.getName() + ", expected " + EVENT_HANDLER_METHOD_NUM_PARAMETERS + " parameter/s, got " + eventHandler.getParameterCount());
        }

        Subscribe subscribeAnn = eventHandler.getAnnotation(Subscribe.class);
        eventListenerRecords.add(new EventListenerRecord(eventListener, eventHandler,
                buildEventMatcher(eventHandler), subscribeAnn.delayed()));

        logger.trace("Subscribing: '" + eventHandler + "'");
    }

    private EventMatcher buildEventMatcher(final Method eventHandler) throws EventRegistrationException {
        Subscribe subscribeAnn = eventHandler.getAnnotation(Subscribe.class);
        Class<? extends EventMatcher> eventMatcherType = subscribeAnn.matches().matcher();
        String[] matcherConstructorParams = subscribeAnn.matches().value();

        Class<?> eventType = eventHandler.getParameterTypes()[EVENT_HANDLER_METHOD_EVENT_TYPE_PARAMETER_INDEX];

        try {
            EventMatcher eventMatcher = (eventMatcherType.getConstructor()).newInstance();
            (eventMatcherType.getMethod(EVENT_MATCHER_INITIALIZER, Class.class, String[].class))
                    .invoke(eventMatcher, eventType, matcherConstructorParams);
            return eventMatcher;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new EventRegistrationException("Cannot create event matcher '" + eventMatcherType + "'", e);
        }
    }
}
