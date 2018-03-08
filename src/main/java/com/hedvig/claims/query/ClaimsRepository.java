package com.hedvig.claims.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimsRepository extends JpaRepository<ClaimEntity, String> {
    Optional<ClaimEntity> findById(String s);
    
    List<ClaimEntity> findByUserId(String s);

    Long countByState(String state);
}
