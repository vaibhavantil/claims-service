package com.hedvig.claims.services;

import com.hedvig.claims.ClaimServiceTestConfiguration;
import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.query.ClaimEntity;
import com.hedvig.claims.web.dto.ClaimSortColumn;
import com.hedvig.claims.web.dto.ClaimsSearchRequestDTO;
import com.hedvig.claims.web.dto.ClaimsSearchResultDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ClaimServiceTestConfiguration.class)
@DataJpaTest(includeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ClaimsQueryService.class)
)
public class ClaimsQueryServiceTest {
  @Autowired
  TestEntityManager entityManager;

  @Autowired
  ClaimsQueryService claimsQueryService;

  private void init() {
    createClaim("82f4335e", "10001", "27-09-2018 13:06:19", OPEN, null, null );
    createClaim("aeec5b8d", "10002", "27-09-2018 15:19:10", OPEN, "Theft - Bike", 100.0 );
    createClaim("713a7e1b", "10003", "27-09-2018 18:26:00", OPEN, null, null );
    createClaim("ebd47f2f", "10004", "28-09-2018 10:01:22", CLOSED, "Water Damage - Kitchen", 10.0 );
    createClaim("1f910a7e", "10005", "28-09-2018 11:08:57", OPEN, "Assault", 200.0 );
    createClaim("fd90ff85", "10002", "28-09-2018 14:17:43", REOPENED, "Water Damage - Bathroom", null );
    createClaim("097e2076", "10004", "29-09-2018 14:20:09", CLOSED, "Travel - Delayed Luggage", null );
    createClaim("61fc4181", "10006", "29-09-2018 17:38:26", REOPENED, "Theft - Bike", null );
  }

  @Test
  public void unpagedNoOrdering() {
    init();
    ClaimsSearchResultDTO res = search(null, null, null, null);
    assertThat(res.getClaims().size()).isEqualTo(8);
    assertThat(res.getPage()).isNull();
    assertThat(res.getTotalPages()).isNull();
  }

  @Test
  public void unpagedOrderByDateAsc() {
    init();
    ClaimsSearchResultDTO res = search(null, null, ClaimSortColumn.DATE, Sort.Direction.ASC);
    assertThat(res.getClaims().size()).isEqualTo(8);
    assertThat(res.getClaims().get(0).getRegistrationDateInstant()).isEqualTo(parseToInstant("27-09-2018 13:06:19"));
    assertThat(res.getClaims().get(7).getRegistrationDateInstant()).isEqualTo(parseToInstant("29-09-2018 17:38:26"));
  }

  @Test
  public void unpagedOrderByDateDesc() {
    init();
    ClaimsSearchResultDTO res = search(null, null, ClaimSortColumn.DATE, Sort.Direction.DESC);
    assertThat(res.getClaims().size()).isEqualTo(8);
    assertThat(res.getClaims().get(0).getRegistrationDateInstant()).isEqualTo(parseToInstant("29-09-2018 17:38:26"));
    assertThat(res.getClaims().get(7).getRegistrationDateInstant()).isEqualTo(parseToInstant("27-09-2018 13:06:19"));
  }

  @Test
  public void unpagedOrderByReservesAsc() {
    init();
    ClaimsSearchResultDTO res = search(null, null, ClaimSortColumn.RESERVES, Sort.Direction.ASC);
    assertThat(res.getClaims().size()).isEqualTo(8);
    assertThat(res.getClaims().get(0).reserve).isEqualTo(10.0);
    assertThat(res.getClaims().get(2).reserve).isEqualTo(200.0);
    assertThat(res.getClaims().get(3).reserve).isNull();
  }

  @Test
  public void unpagedOrderByReservesDesc() {
    init();
    ClaimsSearchResultDTO res = search(null, null, ClaimSortColumn.RESERVES, Sort.Direction.DESC);
    assertThat(res.getClaims().size()).isEqualTo(8);
    assertThat(res.getClaims().get(0).reserve).isEqualTo(200.0);
    assertThat(res.getClaims().get(2).reserve).isEqualTo(10.0);
    assertThat(res.getClaims().get(3).reserve).isNull();
  }

  @Test
  public void pagedOrderByStateAsc() {
    init();
    ClaimsSearchResultDTO res = search(0, 3, ClaimSortColumn.STATE, Sort.Direction.ASC);
    assertThat(res.getClaims().size()).isEqualTo(3);
    assertThat(res.getTotalPages()).isEqualTo(3);
    assertThat(res.getClaims().get(0).state).isEqualTo(CLOSED);
    assertThat(res.getClaims().get(1).state).isEqualTo(CLOSED);
    assertThat(res.getClaims().get(2).state).isEqualTo(OPEN);
  }

  @Test
  public void pagedOrderByStateDesc() {
    init();
    ClaimsSearchResultDTO res = search(1, 3, ClaimSortColumn.STATE, Sort.Direction.DESC);
    assertThat(res.getClaims().size()).isEqualTo(3);
    assertThat(res.getTotalPages()).isEqualTo(3);
    assertThat(res.getClaims().get(0).state).isEqualTo(OPEN);
    assertThat(res.getClaims().get(1).state).isEqualTo(OPEN);
    assertThat(res.getClaims().get(2).state).isEqualTo(OPEN);
  }

  @Test
  public void pagedOrderByTypeAsc() {
    init();
    ClaimsSearchResultDTO res = search(1, 2, ClaimSortColumn.TYPE, Sort.Direction.ASC);
    assertThat(res.getClaims().size()).isEqualTo(2);
    assertThat(res.getTotalPages()).isEqualTo(4);
    assertThat(res.getClaims().get(0).type).isEqualTo("Theft - Bike");
    assertThat(res.getClaims().get(1).type).isEqualTo("Travel - Delayed Luggage");
  }

  @Test
  public void pagedOrderByTypeDesc() {
    init();
    ClaimsSearchResultDTO res = search(0, 2, ClaimSortColumn.TYPE, Sort.Direction.DESC);
    assertThat(res.getClaims().size()).isEqualTo(2);
    assertThat(res.getTotalPages()).isEqualTo(4);
    assertThat(res.getClaims().get(0).type).isEqualTo("Water Damage - Kitchen");
    assertThat(res.getClaims().get(1).type).isEqualTo("Water Damage - Bathroom");
  }

  private ClaimsSearchResultDTO search(Integer page, Integer pageSize, ClaimSortColumn sortBy, Sort.Direction sortDirection) {
    ClaimsSearchRequestDTO req = new ClaimsSearchRequestDTO(page, pageSize, sortBy, sortDirection);
    return claimsQueryService.search(req);
  }
  private void createClaim(String id, String userId, String regDate, ClaimsAggregate.ClaimStates state, String type, Double reserve) {
    ClaimEntity ent = new ClaimEntity();
    ent.id = id;
    ent.userId = userId;
    ent.audioURL = "http://audio.local/rec/" + id;
    ent.registrationDate = parseToInstant(regDate);
    ent.state = state;
    ent.type = type;
    ent.reserve = reserve;

    entityManager.persist(ent);
  }

  private static Instant parseToInstant(String date) {
    return Instant.from(dateFmt.parse(date));
  }

  static final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneId.of("UTC"));
}
