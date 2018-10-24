package com.hedvig.claims.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimsSearchRequestDTO {
    Integer page;
    Integer pageSize;
    ClaimSortColumn sortBy;
    Sort.Direction sortDirection;
}
