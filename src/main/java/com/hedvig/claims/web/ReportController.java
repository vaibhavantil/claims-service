package com.hedvig.claims.web;

import com.hedvig.claims.services.ReportGenerationService;
import com.hedvig.claims.web.dto.ReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.Objects;

@RestController
@RequestMapping("/report")
public class ReportController {

  @Autowired
  private ReportGenerationService reportGenerationService;

  @GetMapping
  public ResponseEntity<ReportDTO> getMonthlyReport(@RequestParam YearMonth yearMonth) {
    return ResponseEntity.ok(Objects.requireNonNull(reportGenerationService.generateReport(yearMonth)));
  }
}
