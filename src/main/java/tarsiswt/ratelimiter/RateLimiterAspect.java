package tarsiswt.ratelimiter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class RateLimiterAspect {
    private static final Logger log = LoggerFactory.getLogger(RateLimiterAspect.class);
    private final Map<String, Limiter> limitStrategies;

    @Autowired
    public RateLimiterAspect(@Qualifier("limitStrategies") Map<String, Limiter> limitStrategies) {
        this.limitStrategies = limitStrategies;
    }

    @Before("@annotation(limited)")
    public void limitRateByAcquiringPermit(JoinPoint joinPoint, Limited limited) {
        Limiter limiter = limitStrategies.get(limited.key());

        double timeSlept = limiter.limit();
        if (log.isDebugEnabled() && timeSlept != 0.0d) {
            log.debug("Method {} was blocked for {} seconds by Limiter {}: {}.",
                    joinPoint.getSignature(), timeSlept, limited.key(), limiter);
        }
    }
}
