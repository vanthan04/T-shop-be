package com.apigateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

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
                    // Check verified
                    Object detailsObj = auth.getDetails();
                    boolean verified = false;
                    if (detailsObj instanceof Map<?, ?> details) {
                        verified = Boolean.TRUE.equals(details.get("verified"));
                    }

                    // Check role
                    boolean hasRole = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .anyMatch(authStr -> requiredRoles.stream()
                                    .map(r -> "ROLE_" + r)
                                    .anyMatch(authStr::equals));

                    return new AuthorizationDecision(verified && hasRole);
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

}
