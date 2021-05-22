package tarsiswt.ratelimiter;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.util.concurrent.RateLimiter;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("UnstableApiUsage")
public
class GuavaBasedLimiter implements Limiter {

    protected final RateLimiter rateLimiter;

    protected GuavaBasedLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public static Limiter using(RateLimiter rateLimiter) {
        checkNotNull(rateLimiter);
        return new GuavaBasedLimiter(rateLimiter);
    }

    @Override
    public double limit() {
        return rateLimiter.acquire();
    }

    RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuavaBasedLimiter that = (GuavaBasedLimiter) o;
        return Objects.equal(rateLimiter, that.rateLimiter);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rateLimiter);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("rateLimiter", rateLimiter).toString();
    }
}
