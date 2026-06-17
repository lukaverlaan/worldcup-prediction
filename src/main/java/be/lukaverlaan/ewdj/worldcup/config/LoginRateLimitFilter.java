package be.lukaverlaan.ewdj.worldcup.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 10;
    private static final long WINDOW_MS = 60_000;

    private final ConcurrentHashMap<String, long[]> attempts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (!request.getMethod().equals("POST") || !request.getServletPath().equals("/login")) {
            chain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(request);
        long now = Instant.now().toEpochMilli();

        attempts.compute(ip, (key, timestamps) -> {
            if (timestamps == null) return new long[]{now};
            long[] recent = java.util.Arrays.stream(timestamps)
                    .filter(t -> now - t < WINDOW_MS)
                    .toArray();
            long[] updated = new long[recent.length + 1];
            System.arraycopy(recent, 0, updated, 0, recent.length);
            updated[recent.length] = now;
            return updated;
        });

        long[] timestamps = attempts.get(ip);
        long recentCount = java.util.Arrays.stream(timestamps)
                .filter(t -> now - t < WINDOW_MS)
                .count();

        if (recentCount > MAX_ATTEMPTS) {
            response.setStatus(429);
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("""
                    <html><body>
                    <h2>Te veel inlogpogingen</h2>
                    <p>Probeer het over een minuut opnieuw.</p>
                    <a href="/login">Terug</a>
                    </body></html>
                    """);
            return;
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
