# ratelimiter

A convenient Spring library that allows you to limit the rate at which annotated methods are called. Based
on [Guava's](https://github.com/google/guava) [`RateLimiter`](https://github.com/google/guava/blob/master/guava/src/com/google/common/util/concurrent/RateLimiter.java)
class.

## Example

First, add this spring starter to your project's classpath. Then configure a `key` with a rate limit (given in *permits
per second* or *queries per second* as its sometimes called) in your configuration file. The example below configures
the `demo-group` key to a limit of 10 permits per second.

```properties
tarsiswt.ratelimiter.configuration.demo-group.qps=1
```

Finally add the `@Limited` annotation to the methods you wish apply the rate limit with the given key:

```Java

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

@Component
class MyRateLimitedComponent {
    @Limited(key = "demo-group")
    public void rateLimitedMethod() {
    }
}
```

Calls made to the `rateLimitedMethod` will now be subjected to the configured rate limit for its key (`demo-group`) in
this case.

## How it works

An `@Aspect` (especifically [`RateLimiterAspect`](./src/main/java/tarsiswt/ratelimiter/RateLimiterAspect.java)) will
intercept calls to methods annotated with `@Limited` and lookup is corresponding rate limit configuration. Currently
there is only one implementation for the [`Limiter`](./src/main/java/tarsiswt/ratelimiter/Limiter.java) interface and
that is the [`GuavaBasedLimiter`](./src/main/java/tarsiswt/ratelimiter/GuavaBasedLimiter.java) which is essencially a
wraper for
Guava's [`RateLimiter`](https://github.com/google/guava/blob/master/guava/src/com/google/common/util/concurrent/RateLimiter.java)
and so the rate limiting works exactly as described in its Javadoc.
