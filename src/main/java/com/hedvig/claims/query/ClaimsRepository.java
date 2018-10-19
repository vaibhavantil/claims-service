package com.hedvig.claims.query;

import java.util.List;
import java.util.Optional;

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

  Long countByState(ClaimsAggregate.ClaimStates state);

  @Query("SELECT c FROM ClaimEntity c")
  Page<ClaimEntity> search(Pageable p);
}
