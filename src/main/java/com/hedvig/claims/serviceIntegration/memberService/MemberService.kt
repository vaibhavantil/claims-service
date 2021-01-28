package com.hedvig.claims.serviceIntegration.memberService

import com.hedvig.claims.serviceIntegration.memberService.dto.Member

interface MemberService {
    fun getMember(id: String): Member?
}
