package com.hedvig.claims.query;

import com.hedvig.claims.aggregates.Asset;
import com.hedvig.claims.aggregates.ClaimSource;
import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.aggregates.DataItem;
import com.hedvig.claims.aggregates.Note;
import com.hedvig.claims.aggregates.Payment;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.hedvig.claims.util.EnumMapChecker;
import com.hedvig.claims.web.dto.ClaimSortColumn;
import com.hedvig.claims.web.dto.ClaimType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class ClaimEntity {

  private static Logger log = LoggerFactory.getLogger(ClaimEntity.class);

  @Id public String id;
  public String userId;
  public String audioURL;
  public LocalDateTime registrationDate;

  @Enumerated(EnumType.STRING)
  public ClaimsAggregate.ClaimStates state;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "varchar(255) default 'APP'")
  public ClaimSource claimSource;

  public String type;
  public Double reserve;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<DataItem> data;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<Asset> assets;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<Event> events;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<Note> notes;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "claimsId")
  public Set<Payment> payments;

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
