package com.apigateway.config;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.List;

public class VerifiedWithRolesAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    private final List<String> requiredRoles;

    public VerifiedWithRolesAuthorizationManager(List<String> requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
                .filter(Authentication::isAuthenticated)
                .map(auth -> {
                    // Lấy tất cả quyền
                    List<String> authorities = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList();

                    // Có ít nhất 1 role hợp lệ?
                    boolean hasRole = requiredRoles.stream()
                            .map(r -> "ROLE_" + r)
                            .anyMatch(authorities::contains);

                    // Nếu không có role nào đúng => từ chối luôn
                    if (!hasRole) return new AuthorizationDecision(false);

                    // Nếu chỉ có client_user thì mới cần kiểm tra verified
                    boolean isUser = authorities.contains("ROLE_client_user");
                    boolean isAdmin = authorities.contains("ROLE_client_admin");

                    boolean verified = true; // mặc định true cho admin
                    if (isUser && !isAdmin) {
                        // chỉ khi là user (không phải admin), mới cần verified
                        Object principal = auth.getPrincipal();
                        if (principal instanceof Jwt jwt) {
                            verified = Boolean.TRUE.equals(jwt.getClaim("verified"));
                        }
                    }

                    return new AuthorizationDecision(verified);
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}

