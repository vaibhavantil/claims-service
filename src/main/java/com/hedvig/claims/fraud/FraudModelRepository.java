package com.hedvig.claims.fraud;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudModelRepository extends JpaRepository<FraudModel, String> {
    Optional<FraudModel> findById(String s);
}
