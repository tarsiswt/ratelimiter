package tarsiswt.ratelimiter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.*;

@SpringBootTest
class MultipleLimitedMethodsOnASingleComponentTest {
    @Autowired
    Component component;
    @MockBean
    Limiter defaultLimiter;

    @Test
    void limitShouldBeCalledOnceForLimitedAnnotatedMethodInDefaultKey() {
        when(defaultLimiter.limit()).thenReturn(0.0);
        component.aLimitedMethod();
        verify(defaultLimiter).limit();
    }

    @Test
    void limitShouldBeCalledOnceForEveryCallToAnnotatedMethodWithDefaultKey() {
        when(defaultLimiter.limit()).thenReturn(0.0);
        component.aLimitedMethod();
        component.anotherLimitedMethod();
        verify(defaultLimiter, times(2)).limit();
    }

    @Test
    void limitShouldBeCalledForRepeatedCallsToAnnotatedMethodWithDefaultKey() {
        when(defaultLimiter.limit()).thenReturn(0.0);
        component.aLimitedMethod();
        component.aLimitedMethod();
        component.aLimitedMethod();
        verify(defaultLimiter, times(3)).limit();
    }

    @TestConfiguration
    static class ContextConfiguration {
        @Bean
        public Component theComponent() {
            return new Component();
        }
    }

    static class Component {
        @Limited
        public void aLimitedMethod() {
        }

        @Limited
        public void anotherLimitedMethod() {
        }
    }
}

