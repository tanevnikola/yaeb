package ft.yaeb;

import ft.yaeb.annotation.Matches;
import ft.yaeb.annotation.Subscribe;
import ft.yaeb.exception.EventRegistrationException;
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

        EventBus eventPublisher = new ThreadPoolEventBus(10);
        eventPublisher.register(instance);
        eventPublisher.publish(2);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        eventPublisher.publish(2);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        eventPublisher.publish(2);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        eventPublisher.publish(2);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        eventPublisher.publish(2);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        eventPublisher.publish(2);
        eventPublisher.publish("3");
        eventPublisher.shutdown();
        try {
            Thread.sleep(350);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
