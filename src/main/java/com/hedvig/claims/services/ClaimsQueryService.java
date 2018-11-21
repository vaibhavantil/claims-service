package com.hedvig.claims.services;

import com.hedvig.claims.query.ClaimEntity;
import com.hedvig.claims.query.ClaimsRepository;
import com.hedvig.claims.util.PageableBuilder;
import com.hedvig.claims.web.dto.ClaimDTO;
import com.hedvig.claims.web.dto.ClaimsSearchRequestDTO;
import com.hedvig.claims.web.dto.ClaimsSearchResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimsQueryService {

  final ClaimsRepository claimsRepository;

  public ClaimsQueryService(ClaimsRepository claimsRepository) {
    this.claimsRepository = claimsRepository;
  }

  @Transactional
  public ClaimsSearchResultDTO search(ClaimsSearchRequestDTO request) {
    PageableBuilder b = new PageableBuilder();

    if (request.getPage() != null && request.getPageSize() != null) {
      b.paged(request.getPage(), request.getPageSize());
    }

    if (request.getSortBy() != null) {
      String sortProp = ClaimEntity.SORT_COLUMS_MAPPING.get(request.getSortBy());
      b.orderBy(sortProp, request.getSortDirection(), Sort.NullHandling.NULLS_LAST);
    }

    Page<ClaimEntity> res = claimsRepository.search(b.build());

    List<ClaimDTO> dtos = res.getContent().stream()
      .map(e -> new ClaimDTO(e.id, e.userId, e.state, e.reserve, e.type, e.audioURL, e.registrationDate, e.claimSource))
      .collect(Collectors.toList());

    if (res.getPageable().isPaged()) {
      return new ClaimsSearchResultDTO(dtos, request.getPage(), res.getTotalPages());
    } else {
      return new ClaimsSearchResultDTO(dtos, null, null);
    }
  }
}
