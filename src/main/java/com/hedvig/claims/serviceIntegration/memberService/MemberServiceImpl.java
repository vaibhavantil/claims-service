package com.hedvig.claims.serviceIntegration.memberService;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
public class MemberServiceImpl implements MemberService {

  private final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);
  private final MemberServiceClient memberServiceClient;

  public MemberServiceImpl(
      MemberServiceClient memberServiceClient) {
    this.memberServiceClient = memberServiceClient;
  }

  @Override
  public Optional<SanctionListStatus> getSanctionListStatus(String memberId) {
    try {
      ResponseEntity<SanctionListStatus> optionalStatus = memberServiceClient
          .getSanctionListStatus(memberId);
      return Optional.of(optionalStatus.getBody());
    } catch (RestClientResponseException ex) {
      if (ex.getRawStatusCode() == 404) {
        return Optional.empty();
      }
      log.error("Could not check sanction list for member {} , {}", memberId, ex);
      throw ex;
    }
  }
}
