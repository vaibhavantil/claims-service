package com.hedvig.claims.serviceIntegration.memberService;

import com.hedvig.claims.serviceIntegration.memberService.dto.Member;
import java.util.Optional;

public interface MemberService {

  Optional<Member> getMember(String memberId);
}
