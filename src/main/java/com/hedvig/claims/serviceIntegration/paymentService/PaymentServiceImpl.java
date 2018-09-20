package com.hedvig.claims.serviceIntegration.paymentService;

import com.hedvig.claims.serviceIntegration.memberService.MemberService;
import com.hedvig.claims.serviceIntegration.memberService.dto.Member;
import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutRequest;
import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutStatus;
import java.util.Optional;
import javax.money.MonetaryAmount;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
public class PaymentServiceImpl implements PaymentService {

  private MemberService memberService;
  private PaymentServiceClient paymentServiceClient;

  public PaymentServiceImpl(MemberService memberService,
      PaymentServiceClient paymentServiceClient) {
    this.memberService = memberService;
    this.paymentServiceClient = paymentServiceClient;
  }

  @Override
  public PayoutStatus executePayment(String memberId, MonetaryAmount amount) {
    Optional<Member> optionalMember = memberService.getMember(memberId);
    if (!optionalMember.isPresent()) {
      return PayoutStatus.Failed;
    }
    Member member = optionalMember.get();

    PayoutRequest request = new PayoutRequest(member, amount);

    try {
      ResponseEntity<?> response = paymentServiceClient.executePayment(request);
      return response.getStatusCode().is2xxSuccessful() ? PayoutStatus.Initiated
          : PayoutStatus.Failed;
    } catch (RestClientResponseException ex) {
      return PayoutStatus.Failed;
    }
  }
}
