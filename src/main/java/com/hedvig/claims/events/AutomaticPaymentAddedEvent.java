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
    public String Id;
    public String claimId;
    public String memberId;
    public MonetaryAmount amount;
    public MonetaryAmount deductible;
    public String note;
    public boolean isExGracia;
    public String handlerReference;
    public boolean sanctionCheckSkipped;
    public Carrier carrier;
}
