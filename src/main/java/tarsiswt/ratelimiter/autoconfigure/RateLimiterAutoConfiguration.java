package tarsiswt.ratelimiter.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tarsiswt.ratelimiter.Limiter;
import tarsiswt.ratelimiter.RateLimiterAspect;
import tarsiswt.ratelimiter.RateLimiterProperties;
import tarsiswt.ratelimiter.RateLimiterProperties.UserProvidedConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.Maps.transformEntries;
import static com.google.common.util.concurrent.RateLimiter.create;
import static tarsiswt.ratelimiter.GuavaBasedLimiter.using;

@SuppressWarnings("UnstableApiUsage")
@EnableConfigurationProperties(RateLimiterProperties.class)
@Configuration
@Import(RateLimiterAspect.class)
public class RateLimiterAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(RateLimiterAutoConfiguration.class);
    private static final String DEFAULT_LIMITER_KEY = "";

    private final Supplier<Limiter> defaultLimiterSupplier;
    private final RateLimiterProperties properties;

    public RateLimiterAutoConfiguration(RateLimiterProperties properties) {
        this.properties = properties;
        this.defaultLimiterSupplier = memoize(() -> using(create(properties.getDefaultConfiguration().getQps())));
    }

    @Bean
    @ConditionalOnMissingBean
    public Limiter defaultConfigurationLimiter() {
        Limiter limiter = defaultLimiterSupplier.get();
        if (log.isDebugEnabled()) {
            log.debug("No bean of type Limiter found. Registering default one: {}", limiter);
        }
        return limiter;
    }

    @Bean("limitStrategies")
    @ConditionalOnMissingBean(name = "limitStrategies")
    public Map<String, Limiter> limitStrategies(@Autowired(required = false) Limiter defaultLimiter) {
        Map<String, UserProvidedConfiguration> userProvidedConfigurations = properties.getConfiguration();
        Map<String, Limiter> keyLimiterMap =
                new HashMap<>(transformEntries(userProvidedConfigurations, (k, v) -> using(create(v.getQps()))));
        if (defaultLimiter != null) {
            keyLimiterMap.putIfAbsent(DEFAULT_LIMITER_KEY, defaultLimiter);
        }
        if (log.isDebugEnabled()) {
            log.debug("Registering limitStrategies: {}", keyLimiterMap);
        }
        return keyLimiterMap;
    }
}
