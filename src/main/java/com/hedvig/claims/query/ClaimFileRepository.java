package com.hedvig.claims.query;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimFileRepository extends JpaRepository<ClaimFile, UUID> {
}
