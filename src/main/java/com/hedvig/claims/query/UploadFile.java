package com.hedvig.claims.query;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "file_uploads")
public class UploadFile {
  private String id;
  private String fileName;
  private byte[] data;
  public String userId;
  private UUID imageId;
  @ManyToOne
  @JoinColumn(name="claims_id", nullable=false)
  private String claimId;
  private String contentType;
  private String metaInfo;
  private long size;
  private String bucket;
  private String key;
  private Boolean markedAsDeleted = false;
  private String markedAsDeletedBy;
  private Instant markedAsDeletedAt;


  @Id @GeneratedValue(generator="id")
  @GenericGenerator(name="id", strategy = "uuid")
  public String getId() {
    return id; }

  public void setId(String id) {
    this.id = id;
  }

  @Column(name = "user_id")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Column(name = "size")
  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  @Column(name = "meta_info")
  public String getMetaInfo() {
    return metaInfo;
  }

  public void setMetaInfo(String metaInfo) {
    this.metaInfo = metaInfo;
  }

  @Column(name = "content_type")
  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @Column(name = "name")
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @Column(name = "image_id")
  public UUID getImageId() {
    return imageId;
  }

  public void setImageId(UUID image_id) {
    this.imageId = image_id;
  }

  @Column(name = "claims_id")
  public String getClaimsId() {
    return claimId;
  }

  public void setClaimsId(String claims_id) {
    this.claimId = claims_id;
  }

  @Column(name = "data")
  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  @Column(name = "bucket")
  public String getBucket() {
    return bucket;
  }

  public void setBucket(String bucket) { this.bucket = bucket; }

  @Column(name = "key")
  public String getKey() { return key; }

  public void setKey(String key) { this.key = key; }

  @Column(name="markedAsDeleted")
  public Boolean getMarkedAsDeleted() {
    return markedAsDeleted;
  }

  public void setMarkedAsDeleted(Boolean markedAsDeleted) {
    this.markedAsDeleted = markedAsDeleted;
  }
  @Column(name="markedAsDeletedBy")
  public String getMarkedAsDeletedBy() {
    return markedAsDeletedBy;
  }

  public void setMarkedAsDeletedBy(String markedAsDeletedBy) {
    this.markedAsDeletedBy = markedAsDeletedBy;
  }

  @Column(name="markedAsDeletedAt")
  public Instant getMarkedAsDeletedAt() {
    return markedAsDeletedAt;
  }

  public void setMarkedAsDeletedAt(Instant markedAsDeletedAt) {
    this.markedAsDeletedAt = markedAsDeletedAt;
  }
}
