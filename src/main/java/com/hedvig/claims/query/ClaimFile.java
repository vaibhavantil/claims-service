package com.hedvig.claims.query;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.springframework.lang.Nullable;

@Entity
@Data
@Table(name = "claim_file")
public class ClaimFile {
  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "name")
  private String fileName;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "uploaded_at")
  private Instant uploadedAt;

  @Column(name = "bucket")
  private String bucket;

  @Column(name = "key")
  private String key;

  @Column(name="marked_as_deleted")
  private Boolean markedAsDeleted = false;

  @Column(name="marked_as_deleted_by")
  @Nullable private String markedAsDeletedBy;

  @Column(name="marked_as_deleted_at")
  @Nullable  private Instant markedAsDeletedAt;

  @Column(name="category")
  @Nullable private String category;
}
