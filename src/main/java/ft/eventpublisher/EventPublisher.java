package ft.eventpublisher;

public interface EventPublisher {
    void publish(Object event);
    void shutdown() throws InterruptedException;
}
