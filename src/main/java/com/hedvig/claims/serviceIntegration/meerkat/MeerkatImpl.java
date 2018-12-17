package com.hedvig.claims.serviceIntegration.meerkat;

import com.hedvig.claims.serviceIntegration.meerkat.dto.MeerkatResponse;
import com.hedvig.claims.serviceIntegration.meerkat.dto.SanctionStatus;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
@Slf4j
public class MeerkatImpl implements Meerkat {

  private MeerkatClient meerkatClient;

  public MeerkatImpl(MeerkatClient meerkatClient) {
    this.meerkatClient = meerkatClient;
  }

  @Override
  public SanctionStatus getMemberSanctionStatus(String fullName) {
    try {
      ResponseEntity<MeerkatResponse> response = meerkatClient.getSanctionListStatus(fullName);

      if (response.getStatusCode().is2xxSuccessful()) {
        return Objects.requireNonNull(response.getBody()).getResult();
      }
      return SanctionStatus.Undetermined;
    } catch (RestClientResponseException ex) {
      log.error("Could not check sanction list for member {} , {}", fullName, ex);
      return SanctionStatus.Undetermined;
    } catch (NullPointerException ex) {
      log.error("Could not check sanction list, response null for member {} , {}", fullName, ex);
      return SanctionStatus.Undetermined;
    }
  }
}
