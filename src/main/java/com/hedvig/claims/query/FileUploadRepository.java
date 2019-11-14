package com.hedvig.claims.query;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepository extends JpaRepository<UploadFile, String> {
  Optional<UploadFile> findByImageId(UUID id);

  List<UploadFile> findAllByClaimsId(String id);
}
