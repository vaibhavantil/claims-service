package com.hedvig.claims.serviceIntegration.meerkat;

import com.hedvig.claims.serviceIntegration.meerkat.dto.SanctionStatus;

public interface Meerkat {

  SanctionStatus getMemberSanctionStatus(String fullName);
}
