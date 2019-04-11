package ft.yaeb.dispatcher;

import ft.yaeb.registry.EventListenerRecord;

@FunctionalInterface
public interface DispatchFacilitator {
    void dispatch(final EventListenerRecord eventListenerRecord, final Object event);
}
