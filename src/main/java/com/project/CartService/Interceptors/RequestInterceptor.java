package com.project.CartService.Interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.logging.Logger;

@Component
public class RequestInterceptor implements HandlerInterceptor {


    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();
    private static final Logger LOG = Logger.getLogger(RequestInterceptor.class.getName());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Extract the Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println(authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token and store it in ThreadLocal
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            tokenHolder.set(token);
            LOG.info("Token set in ThreadLocal: " + token);
        } else {
            LOG.warning("Missing or invalid Authorization header.");
        }

        return true; // Continue processing the request
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Clear the ThreadLocal variable to avoid memory leaks
        tokenHolder.remove();
    }

    public static String getToken() {
        return tokenHolder.get();
    }

}
