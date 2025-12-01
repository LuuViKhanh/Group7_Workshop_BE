package org.example.flyora_backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {

    @Bean
    public PayOS payOS() {
        return new PayOS(
            "8577d57e-7fb0-411a-a9ba-03983ff90afd",      
            "cd395412-7bc7-4ae8-9dcc-25c40d24d39f",
            "11a087c159a4a2bac135224c6a4a3ea6c69eb9707492461cd857fd443c754490"
        );
    }
}
