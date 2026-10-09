package org.example.aispingboot.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.aispingboot.common.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

public final class CurrentUserUtil {

    private CurrentUserUtil() {
    }

    public static Long getUserId() {
        DecodedJWT jwt = getDecodedJwt();
        Long userId = jwt.getClaim("userId").asLong();
        if (userId == null) {
            throw new BusinessException("登录用户信息不完整");
        }
        return userId;
    }

    public static String getUsername() {
        DecodedJWT jwt = getDecodedJwt();
        String username = jwt.getClaim("username").asString();
        return StringUtils.hasText(username) ? username : "用户" + getUserId();
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_2".equals(authority.getAuthority()));
    }

    private static DecodedJWT getDecodedJwt() {
        String token = JwtTokenUtil.getCurrentToken();
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("请先登录");
        }
        return JwtTokenUtil.verifyToken(token);
    }
}
