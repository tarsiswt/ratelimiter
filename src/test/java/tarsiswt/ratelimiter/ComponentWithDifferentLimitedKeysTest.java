package tarsiswt.ratelimiter;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class ComponentWithDifferentLimitedKeysTest {
    @Autowired
    Component component;
    @Resource(name = "limitStrategies")
    Map<String, Limiter> limitStrategies;

    @Test
    void strategiesKeySetShouldContainBothDefaultAndUserProvidedKeys() {
        assertThat(limitStrategies.keySet()).isEqualTo(newHashSet("", "my-group"));
    }

    @Test
    void limitShouldBeCalledOnceForLimitedAnnotatedMethodInDefaultKey() {
        when(limitStrategies.get("").limit()).thenReturn(0.0);
        when(limitStrategies.get("my-group").limit()).thenReturn(0.0);
        component.aLimitedMethod();
        component.aKeyedLimitedMethod();
        verify(limitStrategies.get("")).limit();
        verify(limitStrategies.get("my-group")).limit();
    }

    @TestConfiguration
    static class ContextConfiguration {
        @Bean
        public Component theComponent() {
            return new Component();
        }

        @Bean
        public Map<String, Limiter> limitStrategies() {
            return ImmutableMap.of("", mock(Limiter.class), "my-group", mock(Limiter.class));
        }
    }

    static class Component {
        @Limited
        public void aLimitedMethod() {
        }

        @Limited(key = "my-group")
        public void aKeyedLimitedMethod() {
        }
    }
}

