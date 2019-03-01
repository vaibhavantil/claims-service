package com.hedvig.claims.web;

import com.hedvig.claims.services.ReportGenerationService;
import com.hedvig.claims.web.dto.ReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.Objects;

@RestController
@RequestMapping("/report")
public class ReportController {

  @Autowired
  private ReportGenerationService reportGenerationService;

  @GetMapping("/generate")
  public ResponseEntity<ReportDTO> getMonthlyReport(@RequestParam YearMonth yearMonth) {
    return ResponseEntity.ok(Objects.requireNonNull(reportGenerationService.generateReport(yearMonth)));
  }

  @PutMapping("/replay")
  public ResponseEntity<ReportDTO> replay(@RequestParam YearMonth yearMonth) {

    reportGenerationService.replay(yearMonth);

    return ResponseEntity.accepted().build();
  }

}
