package com.hedvig.claims.Web

import com.hedvig.claims.serviceIntegration.productPricing.Contract
import com.hedvig.claims.serviceIntegration.productPricing.ContractStatus
import com.hedvig.claims.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.claims.services.ProductPricingFacadeImpl
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*


class GetActiveContractsAtTimeOfClaimTest {

    @MockK
    private lateinit var productPricingService: ProductPricingService

    private lateinit var productPricingFacade: ProductPricingFacadeImpl

    @Before
    fun setup() {
        productPricingService = mockk()
        productPricingFacade = ProductPricingFacadeImpl(productPricingService)
    }

    @Test
    fun `should return list with 1 contract when member had 1 active contract at the time of claim`() {
        val activeContract = Contract(
            id = UUID.fromString("E4F822DA-E7C2-40D3-B085-34D6F9B3D8CA"),
            contractStatus = ContractStatus.ACTIVE
        )

        every { productPricingService.getContractsByMemberId("123") } returns listOf(activeContract)

        val contracts = productPricingFacade.getActiveContracts("123")

        assertThat(contracts.size).isEqualTo(1)
    }

    @Test
    fun `should return list with 1 contract when member had 1 active contract and 2 terminated contracts at the time of claim`() {
        val activeContract = Contract(
            id = UUID.fromString("E4F822DA-E7C2-40D3-B085-34D6F9B3D8CA"),
            contractStatus = ContractStatus.ACTIVE
        )

        val terminatedContract1 = Contract(
            id = UUID.fromString("E4F822DA-E7C2-40D3-B085-34D6F9B3D8CA"),
            contractStatus = ContractStatus.TERMINATED
        )

        val terminatedContract2 = Contract(
            id = UUID.fromString("CB5E8298-1290-4B72-B3A3-9E546EA63153"),
            contractStatus = ContractStatus.TERMINATED

        )

        every { productPricingService.getContractsByMemberId("123") } returns listOf(activeContract, terminatedContract1, terminatedContract2)

        val contracts = productPricingFacade.getActiveContracts("123")

        assertThat(contracts.size).isEqualTo(1)
        assertThat(contracts[0].id).isEqualTo(UUID.fromString("E4F822DA-E7C2-40D3-B085-34D6F9B3D8CA"))
    }

    @Test
    fun `should return list with 1 contract when member had 1 active contract and 1 pending contract at the time of claim`() {
        val activeContract = Contract(
            id = UUID.fromString("E4F822DA-E7C2-40D3-B085-34D6F9B3D8CA"),
            contractStatus = ContractStatus.ACTIVE
        )

        val pendingContract = Contract(
            id = UUID.fromString("CB5E8298-1290-4B72-B3A3-9E546EA63153"),
            contractStatus = ContractStatus.PENDING
        )

        every { productPricingService.getContractsByMemberId("123") } returns listOf(activeContract, pendingContract)

        val contracts = productPricingFacade.getActiveContracts("123")

        assertThat(contracts.size).isEqualTo(1)
        assertThat(contracts[0].id).isEqualTo(UUID.fromString("E4F822DA-E7C2-40D3-B085-34D6F9B3D8CA"))
    }

    @Test
    fun `should return list with 2 contracts when member had 2 active contracts and 1 pending contract at the time of claim`() {
        val activeContract1 = Contract(
            id = UUID.fromString("CB5E8298-1290-4B72-B3A3-9E546EA63153"),
            contractStatus = ContractStatus.ACTIVE
        )

        val activeContract2 = Contract(
            id = UUID.fromString("E4F822DA-E7C2-40D3-B085-34D6F9B3D8CA"),
            contractStatus = ContractStatus.ACTIVE
        )

        val pendingContract = Contract(
            id = UUID.fromString("4C51F946-5096-4C32-BE92-93F6DE9EBD5D"),
            contractStatus = ContractStatus.PENDING
        )

        every { productPricingService.getContractsByMemberId("123") } returns
            listOf(
                activeContract1,
                activeContract2,
                pendingContract
            )

        val contracts = productPricingFacade.getActiveContracts("123")

        assertThat(contracts.size).isEqualTo(2)
        assertThat(contracts[0].id).isEqualTo(UUID.fromString("CB5E8298-1290-4B72-B3A3-9E546EA63153"))
        assertThat(contracts[1].id).isEqualTo(UUID.fromString("E4F822DA-E7C2-40D3-B085-34D6F9B3D8CA"))
    }

    @Test
    fun `should return empty list when member had 0 active contracts, 1 terminated contract and 1 pending contract at the time of claim`() {
        val terminatedContract = Contract(
            id = UUID.fromString("E4F822DA-E7C2-40D3-B085-34D6F9B3D8CA"),
            contractStatus = ContractStatus.TERMINATED
        )

        val pendingContract = Contract(
            id = UUID.fromString("4C51F946-5096-4C32-BE92-93F6DE9EBD5D"),
            contractStatus = ContractStatus.PENDING
        )

        every { productPricingService.getContractsByMemberId("123") } returns
                listOf(
                    terminatedContract,
                    pendingContract
                )

        val contracts = productPricingFacade.getActiveContracts("123")

        assertThat(contracts.size).isEqualTo(0)
    }
}
