package com.hedvig.claims.serviceIntegration.meerkat.dto;

import lombok.Value;

@Value
public class MeerkatResponse {

  private String query;
  private SanctionStatus result;
}
