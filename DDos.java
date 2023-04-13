import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS_PER_SECOND = 100;
    private static final long REQUEST_WINDOW_MS = 1000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        if (RateLimiter.acquire(ip)) {
            return true;
        } else {
            response.sendError(429, "Too Many Requests");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Not used
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Not used
    }

    private static class RateLimiter {
        private static final LoadingCache<String, RateLimiter> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(REQUEST_WINDOW_MS, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<String, RateLimiter>() {
                    public RateLimiter load(String key) {
                        return new RateLimiter();
                    }
                });

        private AtomicInteger requests = new AtomicInteger(0);

        private boolean acquire() {
            return requests.incrementAndGet() <= MAX_REQUESTS_PER_SECOND;
        }

        public static boolean acquire(String ip) {
            return cache.getUnchecked(ip).acquire();
        }
    }
}
