package tarsiswt.ratelimiter;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.IntStream;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnstableApiUsage")
@SpringBootTest
class CustomLimitStrategiesTest {
    private static final Logger log = LoggerFactory.getLogger(CustomLimiterBeanTest.class);

    @Resource(name = "limitStrategies")
    Map<String, Limiter> limitStrategies;
    @Autowired
    Component component;

    @Test
    void limitStrategiesContainsUserProvidedKeyAndLimiterIsNotNull() {
        assertThat(limitStrategies.keySet()).isEqualTo(newHashSet("my-group"));
        assertThat(limitStrategies.get("my-group")).isNotNull();
    }

    @Test
    void aComponentWithALimitedMethodGetsRateLimited() {
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        IntStream.rangeClosed(1, 3).forEach(ignored -> component.aLimitedMethod());
        stopWatch.stop();

        log.info(stopWatch.shortSummary());
        assertThat(stopWatch.getTotalTimeMillis()).isGreaterThan(1000);
    }

    @TestConfiguration
    static class ContextConfiguration {
        @Bean
        Map<String, Limiter> limitStrategies() {
            return ImmutableMap.of("my-group", GuavaBasedLimiter.using(RateLimiter.create(1)));
        }

        @Bean
        Component theComponent() {
            return new Component();
        }
    }

    static public class Component {
        @Limited(key = "my-group")
        public void aLimitedMethod() {
        }
    }
}

