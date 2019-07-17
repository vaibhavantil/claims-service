package com.hedvig.claims.serviceIntegration.ticketService.dto;

import com.hedvig.claims.aggregates.ClaimSource;
import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.serviceIntegration.ticketService.TicketStatus;
import com.hedvig.claims.serviceIntegration.ticketService.TicketType;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalTime;


//TODO("MAKE THIS A TICKET DTO - GIVE ENOUGH INFO TO CREATE A TICKET")
@Value
public class TicketDto {
  private String createdBy;
  private String assignedTo;
  @Min(0)
  @Max(1)
  private float priority;
  private TicketType type;
  private LocalDate remindNotificationDate;
  private LocalTime remindNotificationTime;
  private String remindMessage;
  private String description;
  private TicketStatus status;

}
