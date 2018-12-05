package com.hedvig.claims.web.dto;

import java.util.List;
import java.util.UUID;

import lombok.Value;

@Value
public class ClaimsByIdsDTO {
  List<UUID> ids;
}
