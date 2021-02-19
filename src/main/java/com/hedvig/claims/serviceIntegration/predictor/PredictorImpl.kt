package com.hedvig.claims.serviceIntegration.predictor

import org.springframework.stereotype.Service

@Service
class PredictorImpl(
    private val predictorClient: PredictorClient
) : Predictor {

    override fun predictIfItsAccidentClaim(input: String): Boolean {
        return predictorClient.predict(PredictionRequest(input)).body?.response == DRULLE
    }

    companion object {
        const val DRULLE = "drulle"
    }
}
