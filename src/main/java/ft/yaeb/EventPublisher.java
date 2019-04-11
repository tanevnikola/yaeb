package ft.yaeb;

public interface EventPublisher {
    void publish(Object event);
    void shutdown() throws InterruptedException;
}
