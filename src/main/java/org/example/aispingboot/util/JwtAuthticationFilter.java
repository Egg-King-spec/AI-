package org.example.aispingboot.util;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.aispingboot.DTO.response.UserLoginResponseDTO;
import org.example.aispingboot.common.ResultCode;
import org.example.aispingboot.config.SecurityConfig;
import org.example.aispingboot.enumClass.UserStatus;
import org.example.aispingboot.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JwtAuthticationFilter extends OncePerRequestFilter {

    @Resource
    private UserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return SecurityConfig.isPublicPATH(requestUri)
                && !StringUtils.hasText(JwtTokenUtil.extractTokenFromRequest(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = JwtTokenUtil.extractTokenFromRequest(request);
        if (StringUtils.hasText(token)) {
            JwtTokenUtil.TokenVerificationResult validationResult = JwtTokenUtil.validateToken(token);
            if (validationResult != null && validationResult.isValid()) {
                UserLoginResponseDTO.UserDetailResponseDTO user = userService.getUserById(validationResult.getUserId());
                if (user != null && UserStatus.NORMAL.getCode().equals(user.getStatus())) {
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + validationResult.getRoleType())
                    );
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            validationResult.getUsername(),
                            null,
                            authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    request.setAttribute("jwtToken", token);
                } else {
                    clearSecurityContext();
                    ResponseUtil.writeError(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
                    return;
                }
            } else {
                clearSecurityContext();
                ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
                return;
            }
        } else {
            clearSecurityContext();
            ResponseUtil.writeError(response, ResultCode.ACCESS_UNAUTHORIZED);
            return;
        }
        chain.doFilter(request, response);
    }

    private void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
