package com.mmotors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class MMotorsAppApplicationTests {

    @Test
    void contextLoads() {
    }

}
