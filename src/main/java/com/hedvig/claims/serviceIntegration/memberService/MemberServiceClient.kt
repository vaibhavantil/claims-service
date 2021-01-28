package com.hedvig.claims.serviceIntegration.memberService

import com.hedvig.claims.config.FeignConfiguration
import com.hedvig.claims.serviceIntegration.memberService.dto.Member
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(
    name = "memberServiceClient",
    url = "\${hedvig.member-service.url:member-service}",
    configuration = [FeignConfiguration::class]
)
interface MemberServiceClient {
    @GetMapping("/i/member/{memberId}")
    fun getMember(@PathVariable("memberId") memberId: String): ResponseEntity<Member>
}
