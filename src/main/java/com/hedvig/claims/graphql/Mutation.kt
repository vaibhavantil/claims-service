package com.hedvig.claims.graphql

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.hedvig.claims.commands.CreateLuggageClaimCommand
import com.hedvig.claims.graphql.dto.CreateLuggageClaimInput
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.context.GraphQLServletContext
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Component
class Mutation(
        private val commandBus: CommandBus
): GraphQLMutationResolver {
    private val commandGateway = DefaultCommandGateway(commandBus)
    fun createLuggageClaim(
            input: CreateLuggageClaimInput,
            env: DataFetchingEnvironment
    ): Boolean {

        commandGateway.sendAndWait<Void>(
                CreateLuggageClaimCommand(
                        UUID.randomUUID().toString(),
                        env.memberId,
                        LocalDateTime.now(),
                        input.from,
                        input.to,
                        input.hoursDelayed,
                        input.reference,
                        Instant.now()
                )
        )
        return true
    }

    companion object {
        private val DataFetchingEnvironment.memberId: String
            get() {
                val context = getContext<GraphQLServletContext>()
                return context
                        .httpServletRequest
                        .getHeader(HEDVIG_TOKEN)
            }

        private const val HEDVIG_TOKEN = "hedvig.token"
    }
}
