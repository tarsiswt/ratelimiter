package tarsiswt.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnstableApiUsage")
@SpringBootTest
class CustomLimiterBeanTest {
    private static final int PERMITS_PER_SECOND = 1;

    @Autowired
    RateLimiterAspect aspect;
    @Autowired
    Limiter limiter;

    @Test
    void contextLoads() {
        assertThat(aspect).isNotNull();
        assertThat(limiter).isNotNull();
    }

    @Test
    void injectedBeanFromConfigurationIsGuavaBasedLimiter() {
        assertThat(limiter).isInstanceOf(GuavaBasedLimiter.class);
        RateLimiter rateLimiter = ((GuavaBasedLimiter) limiter).getRateLimiter();
        assertThat(rateLimiter.getRate()).isEqualTo(PERMITS_PER_SECOND);
    }

    @TestConfiguration
    static class ContextConfiguration {
        @Bean
        public Limiter rateLimiter() {
            return GuavaBasedLimiter.using(RateLimiter.create(PERMITS_PER_SECOND));
        }
    }
}

