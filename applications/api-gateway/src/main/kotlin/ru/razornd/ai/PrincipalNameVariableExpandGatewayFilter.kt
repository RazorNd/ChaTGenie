package ru.razornd.ai

import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils.putUriTemplateVariables
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.security.Principal

@Component
class PrincipalNameVariableExpandGatewayFilter : GlobalFilter, Ordered {

    override fun filter(
        exchange: ServerWebExchange,
        chain: GatewayFilterChain
    ): Mono<Void> = mono { expandVariables(exchange) }.then(chain.filter(exchange))

    private suspend fun expandVariables(exchange: ServerWebExchange) {
        val principal = exchange.getPrincipal<Principal>().awaitSingleOrNull() ?: return

        putUriTemplateVariables(exchange, mapOf("principal_name" to principal.name))
    }

    override fun getOrder() = 0
}
