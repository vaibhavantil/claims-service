package com.hedvig.claims.Web;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedvig.claims.ClaimServiceTestConfiguration;
import com.hedvig.claims.commands.AddAutomaticPaymentCommand;
import com.hedvig.claims.commands.CreateClaimCommand;
import com.hedvig.claims.events.AutomaticPaymentFailedEvent;
import com.hedvig.claims.events.AutomaticPaymentInitiatedEvent;
import com.hedvig.claims.query.ClaimEntity;
import com.hedvig.claims.query.ClaimsRepository;
import com.hedvig.claims.serviceIntegration.meerkat.Meerkat;
import com.hedvig.claims.serviceIntegration.meerkat.dto.SanctionStatus;
import com.hedvig.claims.serviceIntegration.memberService.MemberService;
import com.hedvig.claims.serviceIntegration.memberService.dto.Member;
import com.hedvig.claims.serviceIntegration.paymentService.PaymentService;
import com.hedvig.claims.serviceIntegration.paymentService.dto.PaymentResponse;
import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutRequest;
import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus;
import com.hedvig.claims.services.ClaimsQueryService;
import com.hedvig.claims.services.LinkFileFromAppToClaimService;
import com.hedvig.claims.web.dto.PaymentRequestDTO;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.money.MonetaryAmount;
import lombok.val;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.javamoney.moneta.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = ClaimServiceTestConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InternalControllerTest {

  private static final String MEMBER_ID = "12345";
  private static final MonetaryAmount CLAIM_PAYOUT_AMOUNT = Money.of(10000, "SEK");
  private static final MonetaryAmount CLAIM_PAYOUT_DEDUCTABLE = Money.of(1500, "SEK");
  private static final String HEDVIG_HANDLER = "aristomachos@hedvig.con";
  private static final UUID TRANSACTION_ID = UUID
    .fromString("a0ac4158-c249-11e8-bdd4-83118ca7bf46");

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EventStore eventStore;

  @Autowired
  private CommandGateway commandGateway;

  @MockBean
  private PaymentService paymentService;

  @MockBean
  private ClaimsQueryService claimsQueryService;

  @MockBean
  private Meerkat meerkat;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private MemberService memberService;

  @MockBean
  private ClaimsRepository claimsRepository;

  @MockBean
  private LinkFileFromAppToClaimService linkFileFromAppToClaimService;

  @Test
  public void Should_ReturnAnInitiatedPaymentEvent_WhenPaymentSuccessfullyCompletedFromPaymentService() {

    UUID CLAIM_ID = UUID.fromString("7e81af56-c234-11e8-baa9-933ede50c2ec");

    given(
      paymentService.executePayment(Mockito.anyString(), Mockito.any(PayoutRequest.class)))
      .willReturn(new PaymentResponse(TRANSACTION_ID, TransactionStatus.INITIATED));

    this.commandGateway.sendAndWait(
      new CreateClaimCommand(CLAIM_ID.toString(), MEMBER_ID, ""));

    this.commandGateway.sendAndWait(new AddAutomaticPaymentCommand(
      CLAIM_ID.toString(),
      MEMBER_ID,
      CLAIM_PAYOUT_AMOUNT,
      CLAIM_PAYOUT_DEDUCTABLE,
      null,
      false,
      HEDVIG_HANDLER,
      false
    ));

    val events = eventStore.readEvents(CLAIM_ID.toString()).asStream().collect(Collectors.toList());

    assertThat(
      events.stream().filter(e -> e.getPayload().getClass().equals(
        AutomaticPaymentInitiatedEvent.class))
        .count()).isEqualTo(1);
  }

  @Test
  public void Should_ReturnAFailedPaymentEvent_WhenPaymentFailedFromPaymentService() {

    UUID CLAIM_ID = UUID.fromString("10b69e8a-c254-11e8-a492-fbcc111e5dbd");

    given(
      paymentService.executePayment(Mockito.anyString(), Mockito.any(PayoutRequest.class)))
      .willReturn(new PaymentResponse(null, TransactionStatus.FAILED));

    this.commandGateway.sendAndWait(
      new CreateClaimCommand(CLAIM_ID.toString(), MEMBER_ID, ""));

    this.commandGateway.sendAndWait(new AddAutomaticPaymentCommand(
      CLAIM_ID.toString(),
      MEMBER_ID,
      CLAIM_PAYOUT_AMOUNT,
      CLAIM_PAYOUT_DEDUCTABLE,
      null,
      false,
      HEDVIG_HANDLER,
      false
    ));

    val events = eventStore.readEvents(CLAIM_ID.toString()).asStream().collect(Collectors.toList());

    assertThat(
      events.stream()
        .filter(e -> e.getPayload().getClass().equals(AutomaticPaymentFailedEvent.class))
        .count()).isEqualTo(1);
  }

  @Test
  public void Should_ReturnAFailedPaymentEvent_WhenPaymentFailedDueToSanctionListFromPaymentService() {

    UUID CLAIM_ID = UUID.fromString("4061522e-c254-11e8-b083-bfa140542604");

    given(
      paymentService.executePayment(Mockito.anyString(), Mockito.any(PayoutRequest.class)))
      .willReturn(new PaymentResponse(null, TransactionStatus.FORBIDDEN));

    this.commandGateway.sendAndWait(
      new CreateClaimCommand(CLAIM_ID.toString(), MEMBER_ID, ""));

    this.commandGateway.sendAndWait(new AddAutomaticPaymentCommand(
      CLAIM_ID.toString(),
      MEMBER_ID,
      CLAIM_PAYOUT_AMOUNT,
      CLAIM_PAYOUT_DEDUCTABLE,
      null,
      false,
      HEDVIG_HANDLER,
      false
    ));

    val events = eventStore.readEvents(CLAIM_ID.toString()).asStream().collect(Collectors.toList());

    assertThat(
      events.stream()
        .filter(e -> e.getPayload().getClass().equals(AutomaticPaymentFailedEvent.class))
        .count()).isEqualTo(1);
  }

  @Test
  public void Should_ReturnForbidden_WhenMemberIsTerrorist() throws Exception {

    given(memberService.getMember(Mockito.anyString())).willReturn(Optional.of(makeMember()));

    given(meerkat.getMemberSanctionStatus(Mockito.anyString()))
      .willReturn(SanctionStatus.FullHit);

    mockMvc
      .perform(post("/i/claims/12345/addAutomaticPayment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(
          makePaymentRequestDto(UUID.randomUUID(), false, null))))
      .andExpect(status().isForbidden());
  }

  @Test
  public void Should_ReturnForbidden_WhenMemberIsPartialTerrorist() throws Exception {

    given(memberService.getMember(Mockito.anyString())).willReturn(Optional.of(makeMember()));

    given(meerkat.getMemberSanctionStatus(Mockito.anyString()))
      .willReturn(SanctionStatus.PartialHit);

    mockMvc
      .perform(post("/i/claims/12345/addAutomaticPayment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(
          makePaymentRequestDto(UUID.randomUUID(), false, null))))
      .andExpect(status().isForbidden());
  }

  @Test
  public void Should_ReturnForbidden_WhenMemberStatusIsUndetermined() throws Exception {

    given(memberService.getMember(Mockito.anyString())).willReturn(Optional.of(makeMember()));

    given(meerkat.getMemberSanctionStatus(Mockito.anyString()))
      .willReturn(SanctionStatus.Undetermined);

    mockMvc
      .perform(post("/i/claims/12345/addAutomaticPayment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(
          makePaymentRequestDto(UUID.randomUUID(), false, null))))
      .andExpect(status().isForbidden());
  }

  @Test
  public void Should_ReturnSuccess_WhenMemberSeemsLikeTerroristButWeWantToBypassMeerkat()
    throws Exception {

    UUID CLAIM_ID = UUID.fromString("733c5cbe-f7d5-11e8-a18f-4b0bf766f99d");

    this.commandGateway.sendAndWait(
      new CreateClaimCommand(CLAIM_ID.toString(), MEMBER_ID, "TEST"));

    given(memberService.getMember(Mockito.anyString())).willReturn(Optional.of(makeMember()));

    given(meerkat.getMemberSanctionStatus(Mockito.anyString()))
      .willReturn(SanctionStatus.PartialHit);

    given(
      paymentService.executePayment(Mockito.anyString(), Mockito.any(PayoutRequest.class)))
      .willReturn(new PaymentResponse(TRANSACTION_ID, TransactionStatus.INITIATED));

    given(claimsRepository.findById(Mockito.anyString()))
      .willReturn(Optional.of(makeClaimEntity(CLAIM_ID.toString())));

    mockMvc
      .perform(post("/i/claims/12345/addAutomaticPayment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(
          makePaymentRequestDto(CLAIM_ID, true, "its not a terrorist, its a developer"))))
      .andExpect(status().is2xxSuccessful());
  }

  @Test
  public void Should_ReturnForbidden_WhenWeWantToBypassWithoutReason()
    throws Exception {

    UUID CLAIM_ID = UUID.fromString("f5145de8-f7d7-11e8-a3a7-0bfc9d610820");

    this.commandGateway.sendAndWait(
      new CreateClaimCommand(CLAIM_ID.toString(), MEMBER_ID, "TEST"));

    given(memberService.getMember(Mockito.anyString())).willReturn(Optional.of(makeMember()));

    given(meerkat.getMemberSanctionStatus(Mockito.anyString()))
      .willReturn(SanctionStatus.PartialHit);

    given(
      paymentService.executePayment(Mockito.anyString(), Mockito.any(PayoutRequest.class)))
      .willReturn(new PaymentResponse(TRANSACTION_ID, TransactionStatus.INITIATED));

    given(claimsRepository.findById(Mockito.anyString()))
      .willReturn(Optional.of(makeClaimEntity(CLAIM_ID.toString())));

    mockMvc
      .perform(post("/i/claims/12345/addAutomaticPayment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(
          makePaymentRequestDto(CLAIM_ID, true, " "))))
      .andExpect(status().isForbidden());
  }

  private PaymentRequestDTO makePaymentRequestDto(UUID claimId, boolean bypass,
    String reason) {
    return new PaymentRequestDTO(claimId, "12345", Money.of(1234, "SEK"), Money.of(1500, "SEK"),
      "test@hedvig.com", bypass,
      reason, false);
  }

  private Member makeMember() {
    return new Member("12345",
      "Kikos",
      "Kikou",
      LocalDate.of(1989, 02, 17),
      "street",
      "city",
      "12345",
      "SE");
  }

  private ClaimEntity makeClaimEntity(String id) {
    val c = new ClaimEntity();
    c.id = id;
    return c;
  }
}
