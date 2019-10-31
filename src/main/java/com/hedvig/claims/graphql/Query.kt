package com.hedvig.claims.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component

@Component
class Query: GraphQLQueryResolver {
    fun `_`() = true
}
