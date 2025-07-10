package com.brogrammerbrigade.backend.security;

import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.util.AuthUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class GlobalSecurityFilter implements Filter {

    private static final String PROPERTY_CORS_ORIGINS_UI = "cors.origins.ui";
    private static final List<String> PUBLIC_PATHS = Arrays.asList("/events", "/clubs", "/rsvps");
    private static final List<String> PUBLIC_AUTH_PATHS = Arrays.asList("/auth/login", "/auth/signup");

    private final JwtTokenService jwtTokenService = JwtTokenService.getInstance();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            setCorsHeaders(httpRequest, httpResponse);
            if (isPreflightRequest(httpRequest)) {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
            String method = httpRequest.getMethod();

            if (isPublicPath(path, method)) {
                chain.doFilter(request, response);
                return;
            }

            if (!authenticateRequest(httpRequest)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            chain.doFilter(request, response);
        } catch (VisibleException e) {
            handleVisibleException(e, httpResponse);
        } catch (Exception e) {
            handleGenericException(e, httpResponse);
        }
    }

    private void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String corsOrigins = System.getProperty(PROPERTY_CORS_ORIGINS_UI);
        response.setHeader("Access-Control-Allow-Origin", corsOrigins);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");

        // Handle preflight caching
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Max-Age", "3600");
        }
    }
    private boolean isPreflightRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    private boolean isPublicPath(String path, String method) {
        if (PUBLIC_AUTH_PATHS.contains(path) && "POST".equalsIgnoreCase(method)) {
            return true;
        }

        return PUBLIC_PATHS.stream().anyMatch(path::startsWith) && "GET".equalsIgnoreCase(method);
    }

    private boolean authenticateRequest(HttpServletRequest request) {
        String token = AuthUtil.extractToken(request);
        if (token != null && jwtTokenService.validateToken(token)) {
            setUserAttributes(request, token);
            return true;
        }
        return false;
    }

    private void setUserAttributes(HttpServletRequest request, String token) {
        request.setAttribute("userId", jwtTokenService.getUserIdFromToken(token));
        request.setAttribute("userRole", jwtTokenService.getRoleFromToken(token));
        request.setAttribute("authorities", jwtTokenService.getAuthoritiesFromToken(token));
    }

    private void handleVisibleException(VisibleException e, HttpServletResponse response) throws IOException {
        System.out.println("Caught VisibleException in GlobalSecurityFilter: " + e.getMessage());
        response.sendError(e.getStatusCode(), e.getMessage());
    }

    private void handleGenericException(Exception e, HttpServletResponse response) throws IOException {
        System.out.println("Caught generic exception in GlobalSecurityFilter: " + e.getClass().getName());
        e.printStackTrace();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }
}