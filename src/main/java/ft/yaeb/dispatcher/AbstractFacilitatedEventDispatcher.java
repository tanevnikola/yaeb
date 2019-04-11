package ft.yaeb.dispatcher;

import ft.yaeb.registry.EventListenerRecord;

public abstract class AbstractFacilitatedEventDispatcher extends AbstractEventDispatcher {
    private final DispatchFacilitator dispatchFacilitator;

    public AbstractFacilitatedEventDispatcher(final DispatchFacilitator dispatchFacilitator) {
        this.dispatchFacilitator = dispatchFacilitator;
    }

    protected void facilitateDispatch(final EventListenerRecord eventListenerRecord, final Object event) {
        dispatchFacilitator.dispatch(eventListenerRecord, event);
    }
}
