package uz.platform.forestyapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {
    @Bean
    SecurityAuditingImpl auditing(){
        return new SecurityAuditingImpl();
    }
}
