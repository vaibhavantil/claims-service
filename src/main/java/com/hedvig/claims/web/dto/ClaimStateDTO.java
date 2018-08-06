package com.hedvig.claims.web.dto;

import com.hedvig.claims.aggregates.ClaimsAggregate;

public class ClaimStateDTO extends HedvigBackofficeDTO {

  public ClaimsAggregate.ClaimStates state;
}
