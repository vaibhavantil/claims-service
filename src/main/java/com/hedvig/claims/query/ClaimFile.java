package com.hedvig.claims.query;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.springframework.lang.Nullable;

@Entity
@Data
@Table(name = "claim_file")
public class ClaimFile {
  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @Column(name = "name")
  private String fileName;

  @Column(name = "contentType")
  private String contentType;

  @Column(name = "uploadedAt")
  private Instant uploadedAt;

  @Column(name = "bucket")
  private String bucket;

  @Column(name = "key")
  private String key;

  @Column(name="markedAsDeleted")
  private Boolean markedAsDeleted = false;

  @Column(name="markedAsDeletedBy")
  @Nullable private String markedAsDeletedBy;

  @Column(name="markedAsDeletedAt")
  @Nullable  private Instant markedAsDeletedAt;

  @Column(name="category")
  @Nullable private String category;
}
