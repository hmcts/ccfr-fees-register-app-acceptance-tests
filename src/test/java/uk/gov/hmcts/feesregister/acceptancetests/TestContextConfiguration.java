package uk.gov.hmcts.feesregister.acceptancetests;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("uk.gov.hmcts.feesregister")
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class TestContextConfiguration {
}
