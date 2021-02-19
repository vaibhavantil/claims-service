package com.hedvig.claims.serviceIntegration.predictor

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PredictorImpl(
    private val predictorClient: PredictorClient
) : Predictor {

    override fun predictIfItsAccidentClaim(input: String): Boolean {
        return try {
            predictorClient.predict(PredictionRequest(input)).body?.response == DRULLE
        } catch (e: Exception) {
            logger.error("Something went wrong with the predictor while predicting $input Exception $e")
            false
        }
    }

    companion object {
        const val DRULLE = "drulle"
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }
}
