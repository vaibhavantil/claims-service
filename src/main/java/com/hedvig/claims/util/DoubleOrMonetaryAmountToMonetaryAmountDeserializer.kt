package com.hedvig.claims.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException
import javax.money.MonetaryAmount
import org.javamoney.moneta.Money

/**
 * Deserializes Double or MonetaryAmount to MonetaryAmount
 */
class DoubleOrMonetaryAmountToMonetaryAmountDeserializer : JsonDeserializer<MonetaryAmount?>() {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): MonetaryAmount? {
        val token: JsonToken = jsonParser.currentToken
        if (token.isStructStart && token.name == "START_OBJECT") {
            return deserializationContext.readValue<MonetaryAmount>(jsonParser, MonetaryAmount::class.java)
        } else if (token.isScalarValue && token.name == "VALUE_NUMBER_FLOAT") {
            val doubleAmount = deserializationContext.readValue<Double>(jsonParser, Double::class.java)
            return Money.of(doubleAmount, "SEK")
        }
        return null
    }
}
