package com.hedvig.claims.web.dto;

import lombok.Value;

import java.util.stream.Stream;

@Value
public class ReportDTO {
  private Stream<ClaimReportDTO> claims;
}
