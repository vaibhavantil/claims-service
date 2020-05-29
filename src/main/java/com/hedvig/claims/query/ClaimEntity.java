package com.hedvig.claims.query;

import com.hedvig.claims.aggregates.*;
import com.hedvig.claims.util.EnumMapChecker;
import com.hedvig.claims.web.dto.ClaimSortColumn;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Set;

@Entity
public class ClaimEntity {

  private static Logger log = LoggerFactory.getLogger(ClaimEntity.class);

  @Id
  public String id;
  public String userId;
  public String audioURL;
  public Instant registrationDate;

  @Enumerated(EnumType.STRING)
  public ClaimsAggregate.ClaimStates state;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "varchar(255) default 'APP'")
  public ClaimSource claimSource;

  public String type;
  public Double reserve;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<ClaimFile> claimFiles;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<DataItem> data;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<Asset> assets;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<Event> events;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<Note> notes;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<Payment> payments;

  @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
  public boolean coveringEmployee;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<Transcription> transcriptions;

  public void addDataItem(DataItem d) {
    data.add(d);
  }

  public void addEvent(Event e) {
    events.add(e);
  }

  public void addNote(Note n) {
    notes.add(n);
  }

  public void addPayment(Payment p) {
    payments.add(p);
  }

  public void addClaimFile(ClaimFile claimFile) {
    claimFiles.add(claimFile);
  }

  public void addAsset(Asset a) {
    assets.add(a);
  }

  public static EnumMap<ClaimSortColumn, String> SORT_COLUMS_MAPPING = new EnumMap<ClaimSortColumn, String>(ClaimSortColumn.class) {{
    put(ClaimSortColumn.DATE, "registrationDate");
    put(ClaimSortColumn.TYPE, "type");
    put(ClaimSortColumn.STATE, "state");
    put(ClaimSortColumn.RESERVES, "reserve");

    EnumMapChecker.ensureMapContainsAllEnumVals(this, ClaimSortColumn.class);
  }};
}
