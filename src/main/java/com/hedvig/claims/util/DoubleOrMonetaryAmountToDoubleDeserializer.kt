package com.hedvig.claims.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException
import javax.money.MonetaryAmount

/**
 * Deserializes Double or MonetaryAmount to Double
 */
class DoubleOrMonetaryAmountToDoubleDeserializer : JsonDeserializer<Double?>() {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Double? {
        val token: JsonToken = jsonParser.currentToken

        if (token.isStructStart && token.name == "START_OBJECT") {
            return deserializationContext
                .readValue<MonetaryAmount>(jsonParser, MonetaryAmount::class.java)
                .number
                .doubleValueExact()

        } else if (token.isScalarValue && token.name == "VALUE_NUMBER_FLOAT") {
            return deserializationContext
                .readValue<Double>(jsonParser, Double::class.java)
        }
        return null
    }
}
