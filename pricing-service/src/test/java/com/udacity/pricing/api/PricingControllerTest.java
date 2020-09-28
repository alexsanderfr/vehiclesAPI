package com.udacity.pricing.api;

import com.udacity.pricing.domain.price.Price;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PricingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testPrice() throws Exception {
        Price price = getPrice();
        URI uri = URI.create("/services/price");
        mvc.perform(
                get(uri)
                        .param("vehicleId", String.valueOf(price.getVehicleId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    private Price getPrice() {
        Price price = new Price();
        price.setVehicleId(1L);
        price.setCurrency("USD");
        price.setPrice(BigDecimal.valueOf(1800.0));
        return price;
    }
}