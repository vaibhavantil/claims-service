package com.hedvig.claims;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedvig.claims.web.dto.PaymentDTO;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ScratchTestJava {
    @Autowired
    private ObjectMapper objectMapper;

    String paymentDtoJson = "{\n" +
        "  \"id\": \"48a63ec9-f841-4de3-9a31-2be60e1d2b67\",\n" +
        "  \"claimID\": \"3addcbb3-836c-49f3-bc57-406fa887114e\",\n" +
        "  \"date\": \"2021-02-08T12:56:52.461591\",\n" +
        "  \"dateInstant\": null,\n" +
        "  \"userId\": \"107255307\",\n" +
        "  \"amount\": {\n" +
        "    \"amount\": \"1000.00\",\n" +
        "    \"currency\": \"SEK\"\n" +
        "  },\n" +
        "  \"deductible\": 100.00,\n" +
        "  \"note\": \"manual test\",\n" +
        "  \"payoutDate\": \"2021-02-08T13:56:52.461591\",\n" +
        "  \"exGratia\": false,\n" +
        "  \"type\": \"Manual\",\n" +
        "  \"handlerReference\": \"michael@hedvig.com\",\n" +
        "  \"transactionId\": null,\n" +
        "  \"status\": \"COMPLETED\"\n" +
        "}";

    @Test
    public void wtf() throws IOException {

        PaymentDTO paymentDto = objectMapper.readValue(paymentDtoJson, PaymentDTO.class);

        assertNotNull(paymentDto);
//        assertNotNull(paymentDto.amount);
//        assertEquals(1000.00, paymentDto.amount.getNumber().doubleValueExact(), 0.002);
//        assertEquals("SEK", paymentDto.amount.getCurrency().toString());
//
//        assertNotNull(paymentDto.deductible);
//        assertEquals(100.00, paymentDto.deductible.getNumber().doubleValueExact(), 0.002);
//        assertEquals("SEK", paymentDto.deductible.getCurrency().toString());
    }

}
