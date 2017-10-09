package com.hedvig.generic.mustrename.query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<AssetEntity, String> {
    Optional<AssetEntity> findById(String s);
    
    List<AssetEntity> findByUserId(String s);
}
