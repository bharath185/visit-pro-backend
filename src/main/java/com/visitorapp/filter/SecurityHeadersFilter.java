package com.visitorapp.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResp = (HttpServletResponse) response;
            httpResp.setHeader("X-Content-Type-Options", "nosniff");
            httpResp.setHeader("X-Frame-Options", "DENY");
            httpResp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
            httpResp.setHeader("Pragma", "no-cache");
            httpResp.setHeader("Expires", "0");
            httpResp.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            httpResp.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            httpResp.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=(), payment=()");
        }
        chain.doFilter(request, response);
    }
}
