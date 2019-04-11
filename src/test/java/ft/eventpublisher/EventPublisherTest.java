package ft.eventpublisher;

import ft.eventpublisher.annotation.Matches;
import ft.eventpublisher.annotation.Subscribe;
import ft.eventpublisher.exception.EventRegistrationException;
import org.junit.Test;

public class EventPublisherTest {
    public static class A {
        long timems = System.currentTimeMillis();

        @Subscribe(matches = @Matches(".*"))
        public void someEvent(Integer event) throws InterruptedException {
            System.out.println(event + " D " + (System.currentTimeMillis() - timems));
        }

    };

    @Test
    public void main() throws EventRegistrationException {

        A instance = new A();

        GenericEventPublisher eventPublisher = new GenericEventPublisher(10);
        eventPublisher.register(instance);
        eventPublisher.publish(2);
        eventPublisher.publish("3");
        eventPublisher.shutdown();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
