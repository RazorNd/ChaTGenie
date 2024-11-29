package ru.razornd.ai

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@Configuration(proxyBeanMethods = false)
open class SecurityConfiguration {

    @Bean
    open fun security(http: ServerHttpSecurity, decoder: ReactiveJwtDecoder) = http {
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

        anonymous { disable() }
        logout { disable() }
        csrf { disable() }
        requestCache { disable() }

        authorizeExchange {
            authorize("/actuator/**", permitAll)
            authorize(anyExchange, authenticated)
        }
        oauth2ResourceServer { jwt { jwtDecoder = decoder } }
    }
}
