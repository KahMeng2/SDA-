package com.brogrammerbrigade.backend.util;

import jakarta.servlet.http.HttpServletRequest;

public class AuthUtil {
    public static String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
