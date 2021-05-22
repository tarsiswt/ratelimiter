package tarsiswt.ratelimiter;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EnableAsync
class LimitedAsyncInteractionTest {
    static CountDownLatch latch = new CountDownLatch(2);

    @Autowired
    Component component;
    @MockBean
    Limiter defaultLimiter;
    @Autowired
    Environment environment;

    @BeforeEach
    void assumeExperimentalProfile() {
        Assumptions.assumeTrue(asList(environment.getActiveProfiles()).contains("experimental"));
    }

    @Test
    void limitShouldBeCalledOnceForLimitedAnnotatedMethodInDefaultKey() throws InterruptedException {
        component.aLimitedMethod();
        component.anotherLimitedMethod();
        assertThat(latch.await(500, TimeUnit.MILLISECONDS)).isTrue();
        verify(defaultLimiter, times(2)).limit();
    }

    @TestConfiguration
    static class ContextConfiguration {
        @Bean
        public Component theComponent() {
            return new Component();
        }
    }

    static class Component {
        Logger log = LoggerFactory.getLogger(Component.class);

        @Async
        @Limited
        public void aLimitedMethod() {
            log.debug(Thread.currentThread().getName());
            latch.countDown();
        }

        @Async
        @Limited
        public void anotherLimitedMethod() {
            log.debug(Thread.currentThread().getName());
            latch.countDown();
        }
    }
}

