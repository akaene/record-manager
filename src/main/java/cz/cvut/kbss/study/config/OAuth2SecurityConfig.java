package cz.cvut.kbss.study.config;

import cz.cvut.kbss.study.security.AuthenticationSuccess;
import cz.cvut.kbss.study.security.SecurityConstants;
import cz.cvut.kbss.study.service.ConfigReader;
import cz.cvut.kbss.study.util.ConfigParam;
import cz.cvut.kbss.study.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@ConditionalOnProperty(prefix = "security", name = "provider", havingValue = "oidc")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class OAuth2SecurityConfig {

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2SecurityConfig.class);

    private final AuthenticationSuccess authenticationSuccess;

    private final ConfigReader config;

    @Autowired
    public OAuth2SecurityConfig(AuthenticationSuccess authenticationSuccess, ConfigReader config) {
        this.authenticationSuccess = authenticationSuccess;
        this.config = config;
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        LOG.debug("Using OAuth2/OIDC security.");
        http.oauth2ResourceServer(
                    (auth) -> auth.jwt((jwt) -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())))
            .authorizeHttpRequests((auth) -> auth.anyRequest().permitAll())
            .cors((auth) -> auth.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .logout((auth) -> auth.logoutUrl(SecurityConstants.LOGOUT_URI)
                                  .logoutSuccessHandler(authenticationSuccess));
        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return SecurityConfig.createCorsConfiguration(config);
    }

    private Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
        return source -> {
            final Collection<SimpleGrantedAuthority> authorities = new GrantedAuthoritiesExtractor().convert(source);
            return new JwtAuthenticationToken(source, authorities);
        };
    }

    private class GrantedAuthoritiesExtractor implements Converter<Jwt, Collection<SimpleGrantedAuthority>> {

        public Collection<SimpleGrantedAuthority> convert(Jwt jwt) {
            return (
                    (Map<String, Collection<?>>) jwt.getClaims().getOrDefault(
                            OAuth2SecurityConfig.this.config.getConfig(ConfigParam.OIDC_ROLE_CLAIM,
                                                                       Constants.DEFAULT_OIDC_ROLE_CLAIM),
                            Collections.emptyMap())
            ).getOrDefault("roles", Collections.emptyList())
             .stream()
             .map(Object::toString)
             .map(SimpleGrantedAuthority::new).toList();
        }
    }
}
