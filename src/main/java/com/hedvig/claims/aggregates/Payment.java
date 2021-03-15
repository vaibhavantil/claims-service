package com.hedvig.claims.aggregates;

import com.hedvig.claims.query.Carrier;
import com.hedvig.claims.web.dto.PaymentType;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class Payment {
    @Id
    public String id;
    public LocalDateTime date;
    public String userId;
    public Double amount;
    public Double deductible;
    public String note;
    public LocalDateTime payoutDate;
    public Boolean exGratia;
    @Enumerated(value = EnumType.STRING)
    public PaymentType type;
    public String handlerReference;
    @Enumerated(value = EnumType.STRING)
    public PayoutStatus payoutStatus;
    public UUID payoutReference;
    @Enumerated(value = EnumType.STRING)
    public Carrier carrier;
}
