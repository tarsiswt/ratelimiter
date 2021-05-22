package tarsiswt.ratelimiter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static tarsiswt.ratelimiter.RateLimiterProperties.CONFIGURATION_PREFIX;

@SuppressWarnings("UnstableApiUsage")
@SpringBootTest(properties = {
        CONFIGURATION_PREFIX + ".configuration.my-group.qps=15",
        CONFIGURATION_PREFIX + ".configuration.my-other-group.qps=20",
})
class CustomLimitStrategiesBasedOnPropertiesTest {
    @Resource(name = "limitStrategies")
    Map<String, Limiter> limitStrategies;

    @Test
    void autoConfigurationPicksUpCustomKeyProperties() {
        assertThat(limitStrategies.get("my-group")).isNotNull();
        double myGroupRate = ((GuavaBasedLimiter) limitStrategies.get("my-group")).getRateLimiter().getRate();
        assertThat(myGroupRate).isCloseTo(15.0, within(0.001));

        assertThat(limitStrategies.get("my-other-group")).isNotNull();
        double myOtherGroupRate = ((GuavaBasedLimiter) limitStrategies.get("my-other-group")).getRateLimiter().getRate();
        assertThat(myOtherGroupRate).isCloseTo(20.0, within(0.001));
    }
}

