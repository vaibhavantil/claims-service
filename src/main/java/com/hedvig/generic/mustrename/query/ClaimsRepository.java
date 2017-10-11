package com.hedvig.generic.mustrename.query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimsRepository extends JpaRepository<ClaimEntity, String> {
    Optional<ClaimEntity> findById(String s);
    
    List<ClaimEntity> findByUserId(String s);
}
