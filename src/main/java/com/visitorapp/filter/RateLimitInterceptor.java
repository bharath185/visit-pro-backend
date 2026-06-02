package com.visitorapp.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RateLimitInterceptor.class);

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final long WINDOW_MS = 60_000;

    private final ConcurrentHashMap<String, RateLimitEntry> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = getClientIp(request);
        String key = clientIp + ":" + request.getRequestURI();

        long now = System.currentTimeMillis();
        RateLimitEntry entry = rateLimitMap.compute(key, (k, existing) -> {
            if (existing == null || (now - existing.windowStart) > WINDOW_MS) {
                return new RateLimitEntry(now);
            }
            return existing;
        });

        synchronized (entry) {
            int count = entry.counter.incrementAndGet();
            if (count > MAX_REQUESTS_PER_MINUTE) {
                log.warn("Rate limit exceeded for IP: {} on {}", clientIp, request.getRequestURI());
                response.setStatus(429);
                response.setHeader("Retry-After", String.valueOf(WINDOW_MS / 1000));
                return false;
            }
        }

        response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RateLimitEntry {
        final long windowStart;
        final AtomicInteger counter;

        RateLimitEntry(long windowStart) {
            this.windowStart = windowStart;
            this.counter = new AtomicInteger(0);
        }
    }
}
