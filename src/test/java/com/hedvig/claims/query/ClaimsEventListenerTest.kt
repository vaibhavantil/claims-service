package com.hedvig.claims.query

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates
import com.hedvig.claims.aggregates.DataItem
import com.hedvig.claims.commands.AddDataItemCommand
import com.hedvig.claims.web.dto.ClaimDataType.DataType
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.Before
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.fail

class ClaimsEventListenerTest {

    @MockK(relaxed = true)
    private lateinit var commandGateway: CommandGateway

    private lateinit var claimsEventListenerToTest: ClaimsEventListener

    init {
        MockKAnnotations.init(this)
    }

    @Before
    fun setup() {
        claimsEventListenerToTest = ClaimsEventListener(
            mockk(),
            mockk(),
            mockk(),
            commandGateway
        )
    }

    @Test(expected = NullPointerException::class)
    fun `setDefaultClaimDate with null claimEntity should throw NPE`() {
        claimsEventListenerToTest.setDefaultClaimDate(null)
        fail("Expected a NullPointerException")
    }

    @Test
    fun `setDefaultClaimDate with null claimEntity registrationDate should do nothing`() {
        val claimEntity = ClaimEntity().apply {
            id = "2345"
            userId = "5432"
            contractId = UUID.fromString("72452206-fbad-416b-8a48-7fda7bf2df2c")
            state = ClaimStates.OPEN
        }
        claimsEventListenerToTest.setDefaultClaimDate(claimEntity)

        verify(inverse = true) { commandGateway.send<AddDataItemCommand>(any()) }
    }

    @Test
    fun `setDefaultClaimDate with null claimEntity containing date dataItem should not add or overwrite date`() {
        val dataItem = DataItem().apply {
            date = LocalDateTime.now()
            type = DataType.DATE
        }
        val claimEntity = ClaimEntity().apply {
            id = "3456"
            userId = "6543"
            contractId = UUID.fromString("72452206-fbad-416b-8a48-7fda7bf2df2c")
            state = ClaimStates.OPEN
            registrationDate = Instant.now()
            data = setOf(dataItem)
        }
        claimsEventListenerToTest.setDefaultClaimDate(claimEntity)

        verify(inverse = true) { commandGateway.send<AddDataItemCommand>(any()) }
    }

    @Test
    fun `setDefaultClaimDate should add date dataItem if there is a claim registrationDate and no date dataItem`() {
        val claimEntity = ClaimEntity().apply {
            id = "4567"
            userId = "7654"
            contractId = UUID.fromString("72452206-fbad-416b-8a48-7fda7bf2df2c")
            state = ClaimStates.OPEN
            registrationDate = Instant.now()
            data = emptySet()
        }
        claimsEventListenerToTest.setDefaultClaimDate(claimEntity)

        verify(exactly = 1) { commandGateway.send<AddDataItemCommand>(any()) }
    }
}
