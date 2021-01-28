package com.hedvig.claims.serviceIntegration.memberService

import com.hedvig.claims.serviceIntegration.memberService.dto.Member
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException

@Service
class MemberServiceImpl(
    private val client: MemberServiceClient
) : MemberService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getMember(id: String): Member? {
        return try {
            val response: ResponseEntity<Member> = client.getMember(id)
            response.body
        } catch (ex: RestClientResponseException) {
            if (ex.rawStatusCode == 404) {
                return null
            }
            log.error("Could not find member {} , {}", id, ex)
            throw ex
        }
    }
}
