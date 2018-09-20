package com.hedvig.claims.serviceIntegration.memberService;

import com.hedvig.claims.serviceIntegration.memberService.dto.Member;
import com.hedvig.claims.serviceIntegration.memberService.dto.SanctionStatus;
import java.util.Optional;

public interface MemberService {

  Optional<Member> getMember(String memberId);

  SanctionStatus getMemberSanctionStatus(String memberId);
}