package com.hedvig.generic.mustrename.query;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "file_uploads")
public class UploadFile {
    private long id;
    private String fileName;
    private byte[] data;
    public String userId;
    private UUID imageId;
    private UUID claimsId;
    private String contentType;
    private String metaInfo;
    private long size;
 
    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }
 
    public void setId(long id) {
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
    public UUID getClaimsId() {
        return claimsId;
    }
 
    public void setClaimsId(UUID claims_id) {
        this.claimsId = claims_id;
    }
    
    @Column(name = "data")
    public byte[] getData() {
        return data;
    }
 
    public void setData(byte[] data) {
        this.data = data;
    }
}