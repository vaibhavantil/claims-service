package com.hedvig.claims.Web;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import com.hedvig.claims.ClaimServiceTestConfiguration;
import com.hedvig.claims.commands.AddAutomaticPaymentCommand;
import com.hedvig.claims.commands.CreateClaimCommand;
import com.hedvig.claims.events.AutomaticPaymentFailedEvent;
import com.hedvig.claims.events.AutomaticPaymentInitiatedEvent;
import com.hedvig.claims.serviceIntegration.paymentService.PaymentService;
import com.hedvig.claims.serviceIntegration.paymentService.dto.PaymentResponse;
import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.money.MonetaryAmount;

import com.hedvig.claims.services.ClaimsQueryService;
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


  @Test
  public void Should_ReturnAnInitiatedPaymentEvent_WhenPaymentSuccessfullyCompletedFromPaymentService() {

    String CLAIM_ID = "7e81af56-c234-11e8-baa9-933ede50c2ec";

    given(
        paymentService.executePayment(Mockito.anyString(), Mockito.any(MonetaryAmount.class)))
        .willReturn(new PaymentResponse(TRANSACTION_ID, TransactionStatus.INITIATED));

    this.commandGateway.sendAndWait(
        new CreateClaimCommand(CLAIM_ID, MEMBER_ID, Instant.now(), ""));

    this.commandGateway.sendAndWait(new AddAutomaticPaymentCommand(
        CLAIM_ID.toString(),
        MEMBER_ID,
        CLAIM_PAYOUT_AMOUNT,
        null,
        false,
        HEDVIG_HANDLER
    ));

    val events = eventStore.readEvents(CLAIM_ID).asStream().collect(Collectors.toList());

    assertThat(
        events.stream().filter(e -> e.getPayload().getClass().equals(
            AutomaticPaymentInitiatedEvent.class))
            .count()).isEqualTo(1);
  }

  @Test
  public void Should_ReturnAFailedPaymentEvent_WhenPaymentFailedFromPaymentService() {

    String CLAIM_ID = "10b69e8a-c254-11e8-a492-fbcc111e5dbd";

    given(
        paymentService.executePayment(Mockito.anyString(), Mockito.any(MonetaryAmount.class)))
        .willReturn(new PaymentResponse(null, TransactionStatus.FAILED));

    this.commandGateway.sendAndWait(
        new CreateClaimCommand(CLAIM_ID, MEMBER_ID, Instant.now(), ""));

    this.commandGateway.sendAndWait(new AddAutomaticPaymentCommand(
        CLAIM_ID.toString(),
        MEMBER_ID,
        CLAIM_PAYOUT_AMOUNT,
        null,
        false,
        HEDVIG_HANDLER
    ));

    val events = eventStore.readEvents(CLAIM_ID).asStream().collect(Collectors.toList());

    assertThat(
        events.stream().filter(e -> e.getPayload().getClass().equals(AutomaticPaymentFailedEvent.class))
            .count()).isEqualTo(1);
  }

  @Test
  public void Should_ReturnAFailedPaymentEvent_WhenPaymentFailedDueToSanctionListFromPaymentService() {

    String CLAIM_ID = "4061522e-c254-11e8-b083-bfa140542604";

    given(
        paymentService.executePayment(Mockito.anyString(), Mockito.any(MonetaryAmount.class)))
        .willReturn(new PaymentResponse(null, TransactionStatus.FORBIDDEN));

    this.commandGateway.sendAndWait(
        new CreateClaimCommand(CLAIM_ID.toString(), MEMBER_ID, Instant.now(), ""));

    this.commandGateway.sendAndWait(new AddAutomaticPaymentCommand(
        CLAIM_ID.toString(),
        MEMBER_ID,
        CLAIM_PAYOUT_AMOUNT,
        null,
        false,
        HEDVIG_HANDLER
    ));

    val events = eventStore.readEvents(CLAIM_ID).asStream().collect(Collectors.toList());

    assertThat(
        events.stream().filter(e -> e.getPayload().getClass().equals(AutomaticPaymentFailedEvent.class))
            .count()).isEqualTo(1);
  }

}
