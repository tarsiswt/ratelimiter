package tarsiswt.ratelimiter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(RateLimiterProperties.CONFIGURATION_PREFIX)
public class RateLimiterProperties {
    public static final int DEFAULT_QPS_LIMIT = 60;
    public static final String CONFIGURATION_PREFIX = "tarsiswt.ratelimiter";

    Default defaultConfiguration = new Default();
    Map<String, UserProvidedConfiguration> configuration = new HashMap<>();

    public RateLimiterProperties() {
    }

    public Default getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public Map<String, UserProvidedConfiguration> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, UserProvidedConfiguration> configuration) {
        this.configuration = configuration;
    }

    public static class Default {
        double qps = DEFAULT_QPS_LIMIT;

        public double getQps() {
            return qps;
        }

        public void setQps(double qps) {
            this.qps = qps;
        }
    }

    public static class UserProvidedConfiguration extends Default {
    }
}
