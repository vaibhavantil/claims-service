package com.hedvig.claims.serviceIntegration.predictor

import com.hedvig.claims.config.FeignConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(
    name = "predictorClient",
    url = "\${hedvig.predictor.url:predictor}",
    configuration = [FeignConfiguration::class]
)
interface PredictorClient {
    @RequestMapping(value = ["/predict"], method = [RequestMethod.POST], produces = ["text/html;charset=utf-8"])
    fun predict(@RequestBody request: PredictionRequest): ResponseEntity<String>
}
