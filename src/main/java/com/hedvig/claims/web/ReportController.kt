package com.hedvig.claims.web

import com.hedvig.claims.services.ReportGenerationService
import com.hedvig.claims.web.dto.ReportDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.YearMonth

@RestController
@RequestMapping("/report")
class ReportController {

    @Autowired
    lateinit var reportGenerationService: ReportGenerationService

    @GetMapping
    fun getMonthlyReport(@RequestParam yearMonth: YearMonth): ResponseEntity<ReportDTO> {
        reportGenerationService.generateReport(yearMonth)
        return ResponseEntity.status(HttpStatus.OK).build()
    }
}
