package com.hedvig.claims.config;

import io.sentry.spring.EnableSentry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@EnableSentry()
@Configuration
@Profile("production")
public class SentryConfig {

}
