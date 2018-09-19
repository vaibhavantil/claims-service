package com.hedvig.claims.serviceIntegration.memberService;

import java.util.Optional;

public interface MemberService {

  Optional<SanctionListStatus> getSanctionListStatus(String memberId);

}
