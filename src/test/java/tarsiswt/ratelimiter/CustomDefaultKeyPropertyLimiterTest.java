package tarsiswt.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static tarsiswt.ratelimiter.CustomDefaultKeyPropertyLimiterTest.PERMITS_PER_SECOND;
import static tarsiswt.ratelimiter.RateLimiterProperties.CONFIGURATION_PREFIX;

@SuppressWarnings("UnstableApiUsage")
@SpringBootTest(properties = CONFIGURATION_PREFIX + ".default-configuration.qps=" + PERMITS_PER_SECOND)
class CustomDefaultKeyPropertyLimiterTest {
    static final int PERMITS_PER_SECOND = 1;

    @Autowired
    Limiter limiter;
    @Autowired
    RateLimiterProperties properties;

    @Test
    void defaultProvidedLimiterIsNotNull() {
        assertThat(limiter).isNotNull();
    }

    @Test
    void injectedBeanFromConfigurationIsGuavaBasedLimiter() {
        assertThat(limiter).isInstanceOf(GuavaBasedLimiter.class);
    }

    @Test
    void rateLimiterIsConfiguredWithProvidedConfigurationValue() {
        RateLimiter rateLimiter = ((GuavaBasedLimiter) limiter).getRateLimiter();
        assertThat(rateLimiter.getRate()).isEqualTo(PERMITS_PER_SECOND);
    }

    @Test
    void propertiesArePlacedInConfigurationPropertiesObject() {
        assertThat(properties.getDefaultConfiguration().getQps()).isEqualTo(PERMITS_PER_SECOND);
    }
}