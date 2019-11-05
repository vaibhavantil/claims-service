package com.hedvig.claims.query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.hedvig.claims.aggregates.ClaimsAggregate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimsRepository extends JpaRepository<ClaimEntity, String> {
  Optional<ClaimEntity> findById(String s);

  List<ClaimEntity> findByUserId(String s);

  List<ClaimEntity> findByIdIn(Set<String> ids);

  Long countByState(ClaimsAggregate.ClaimStates state);

  @Query("SELECT c FROM ClaimEntity c")
  Page<ClaimEntity> search(Pageable p);

  @Query("SELECT c from ClaimEntity c where type = :type")
  List<ClaimEntity> findByType(String type);
}
