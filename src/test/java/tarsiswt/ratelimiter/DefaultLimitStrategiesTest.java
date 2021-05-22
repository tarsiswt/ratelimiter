package tarsiswt.ratelimiter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DefaultLimitStrategiesTest {
    @Resource(name = "limitStrategies")
    Map<String, Limiter> limitStrategies;

    @Test
    void limitStrategiesIsPopulatedWithDefaultLimiter() {
        assertThat(limitStrategies).isNotNull();
        assertThat(limitStrategies.keySet()).isEqualTo(newHashSet(""));
    }
}

