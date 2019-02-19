package com.hedvig.claims.web.dto;

import lombok.Value;

import java.util.List;
import java.util.stream.Stream;

@Value
public class ReportDTO {
  private List<ClaimReportDTO> claims;
}
