package com.hedvig.claims.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthCheckController {
  @GetMapping("/health")
  public ResponseEntity<Void> healthCheck() {
    return ResponseEntity.noContent().build();
  }
}
