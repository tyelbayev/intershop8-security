package com.example.intershop.util;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import reactor.core.publisher.Mono;

public class CurrentUser {

    public static Mono<String> getPreferredUsername() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (DefaultOidcUser) ctx.getAuthentication().getPrincipal())
                .mapNotNull(user -> (String) user.getAttribute("preferred_username"));
    }
}
