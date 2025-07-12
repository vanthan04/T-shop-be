package com.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthConverter jwtAuthConverter(){
        return new JwtAuthConverter();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        //Route demo
                        .pathMatchers("/api/demo/**").permitAll()

                        //Route user and auth
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/api/v1/user/**")
                        .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin", "client_user")))

                        //Route product
                        .pathMatchers(HttpMethod.GET, "/api/v1/products/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin", "client_user")))
                        .pathMatchers(HttpMethod.POST, "/api/v1/products/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin")))
                        .pathMatchers(HttpMethod.PUT, "/api/v1/products/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin")))
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/products/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin")))


                        //Route order
                        .pathMatchers(HttpMethod.GET, "/api/v1/orders/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin", "client_user")))
                        .pathMatchers(HttpMethod.POST, "/api/v1/orders/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin", "client_user")))
                        .pathMatchers(HttpMethod.PUT, "/api/v1/orders/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin")))
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/orders/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin")))

                        //Route inventory
                        .pathMatchers(HttpMethod.GET, "/api/v1/inventory/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin", "client_user")))
                        .pathMatchers(HttpMethod.POST, "/api/v1/inventory/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin", "client_user")))
                        .pathMatchers(HttpMethod.PUT, "/api/v1/inventory/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin")))
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/inventory/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin")))

                        //Route cart
                        .pathMatchers("/api/v1/carts/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin", "client_user")))


                        //Route notification
                        .pathMatchers("/api/v1/notification/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin")))

                        //Route payment
                        .pathMatchers(HttpMethod.GET, "/api/v1/payment/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin", "client_user")))
                        .pathMatchers(HttpMethod.POST, "/api/v1/payment/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin")))
                        .pathMatchers(HttpMethod.PUT, "/api/v1/payment/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin")))
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/payment/**")
                            .access(new VerifiedWithRolesAuthorizationManager(List.of("client_admin")))

                        //Other route
                        .anyExchange().authenticated()
                );

        //Xu li exception authorization va forbidden
        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((exchange, ex) -> {
                            var response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                            String body = """
                                    {
                                      "success": false,
                                      "message": "Unauthorized - Bạn chưa đăng nhập hoặc token không hợp lệ",
                                      "dataResponse": null
                                    }
                                """;
                            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                            var buffer = response.bufferFactory().wrap(bytes);
                            return response.writeWith(Mono.just(buffer)).then(Mono.empty()); // 👈 hoặc Mono.error(ex)
                        })
                        .accessDeniedHandler((exchange, ex) -> {
                            var response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                            String body = """
                                    {
                                      "success": 0,
                                      "message": "Forbidden - Bạn không có quyền truy cập",
                                      "dataResponse": null
                                    }
                                """;
                            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                            var buffer = response.bufferFactory().wrap(bytes);
                            return response.writeWith(Mono.just(buffer)).then(Mono.empty()); // 👈
                        })

                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                )
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        return http.build();
    }


    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // React frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
//    @Bean
//    public GlobalFilter logRequestFilter() {
//        return (exchange, chain) -> {
//            System.out.println(">>> PATH: " + exchange.getRequest().getPath());
//            System.out.println(">>> AUTH HEADER: " + exchange.getRequest().getHeaders().getFirst("Authorization"));
//            return chain.filter(exchange);
//        };
//    }


}
