package com.hedvig.claims.events;

import com.hedvig.claims.query.Carrier;
import lombok.Value;
import org.axonframework.serialization.Revision;

@Value
@Revision("3.0")
public class PaymentAddedEvent {
    String id;
    String claimsId;
    String userId;

    Double amount;
    Double deductible;
    String note;
    Boolean exGratia;
    String handlerReference;
    Carrier carrier;
}
