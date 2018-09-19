package com.hedvig.claims.serviceIntegration.memberService;

import com.hedvig.claims.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(
    name = "memberServiceClient",
    url = "${hedvig.member-service.url:member-service}",
    configuration = FeignConfiguration.class)
public interface MemberServiceClient {

  @RequestMapping(value = "/i/member/{memberId}/sanctionList", method = RequestMethod.GET)
  ResponseEntity<SanctionListStatus> getSanctionListStatus(@PathVariable("memberId") String memberId);
}
