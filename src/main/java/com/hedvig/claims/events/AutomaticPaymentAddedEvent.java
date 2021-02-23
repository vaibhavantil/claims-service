package com.hedvig.claims.events;

import com.hedvig.claims.query.Carrier;
import javax.money.MonetaryAmount;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.axonframework.serialization.Revision;

@Value
@AllArgsConstructor
@Revision("1.0")
public class AutomaticPaymentAddedEvent {
    String Id;
    String claimId;
    String memberId;
    MonetaryAmount amount;
    MonetaryAmount deductible;
    String note;
    boolean isExGracia;
    String handlerReference;
    boolean sanctionCheckSkipped;
    Carrier carrier;
}
