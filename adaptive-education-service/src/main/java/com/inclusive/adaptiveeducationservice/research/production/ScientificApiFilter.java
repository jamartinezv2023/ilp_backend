package com.inclusive.adaptiveeducationservice.research.production;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.jwt.JwtException;
import java.io.IOException;

/** Default-deny production boundary: unscoped legacy data APIs cannot bypass authorization. */
public final class ScientificApiFilter implements Filter {
    public static final String ACTOR_ATTRIBUTE = ScientificApiFilter.class.getName() + ".actor";
    private final ScientificTokenVerifier verifier;

    public ScientificApiFilter(ScientificTokenVerifier verifier) { this.verifier = verifier; }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var http = (HttpServletRequest) request;
        var output = (HttpServletResponse) response;
        output.setHeader("Cache-Control", "no-store");
        output.setHeader("X-Content-Type-Options", "nosniff");
        String path = http.getServletPath();
        // MockMvc supplies pathInfo when no servlet mapping is installed.
        if (path.isEmpty()) { path = http.getRequestURI().substring(http.getContextPath().length()); }
        boolean allowed = path.equals("/api/v1/assessment-submissions") && "POST".equals(http.getMethod())
                || path.startsWith("/api/v1/scientific-applications/");
        if (!allowed) { output.sendError(403); return; }
        try {
            request.setAttribute(ACTOR_ATTRIBUTE, verifier.verify(http.getHeader("Authorization")));
        } catch (JwtException exception) {
            output.setHeader("WWW-Authenticate", "Bearer");
            output.sendError(401);
            return;
        }
        chain.doFilter(request, response);
    }
}
