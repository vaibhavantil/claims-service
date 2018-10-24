package com.hedvig.claims.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ClaimsSearchResultDTO {
  List<ClaimDTO> claims;
  Integer page;
  Integer totalPages;
}
