package ilp.r9;

import com.inclusive.authservice.AuthServiceApplication;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.UserAccountRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.inclusive.authservice.security.jwt.JwtProperties;
import com.inclusive.authservice.security.CorsConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.UUID;

/** Test-runtime only: never included in bootJar; real auth service, bcrypt, JWT and security chain. */
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
@EntityScan("com.inclusive.authservice")
@EnableJpaRepositories("com.inclusive.authservice")
@ComponentScan(basePackages = "com.inclusive.authservice", excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {AuthServiceApplication.class, CorsConfig.class})
})
public class R9AuthRuntime {
    public static void main(String[] args) throws Exception {
        var application = new SpringApplication(R9AuthRuntime.class, Fixture.class);
        application.addListeners((org.springframework.context.ApplicationListener<org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent>) event -> {
            var environment = event.getEnvironment();
            if (!"jdbc:h2:mem:r9_18083;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1".equals(environment.getProperty("spring.datasource.url"))
                || !"127.0.0.1".equals(environment.getProperty("server.address"))
                || !"18083".equals(environment.getProperty("server.port"))
                || !"never".equals(environment.getProperty("spring.sql.init.mode"))
                || !"false".equals(environment.getProperty("spring.flyway.enabled"))
                || !"false".equals(environment.getProperty("spring.liquibase.enabled"))) {
                throw new IllegalArgumentException("R9 runtime requires its isolated in-memory configuration");
            }
        });
        var context = application.run(args);
        Files.writeString(Path.of(context.getEnvironment().getRequiredProperty("r9.ready")), "READY");
    }
    @TestConfiguration(proxyBeanMethods = false)
    static class Fixture {
        @Bean CorsConfigurationSource corsConfigurationSource() {
            var configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("http://127.0.0.1:15179"));
            configuration.setAllowedMethods(List.of("GET", "POST"));
            configuration.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Tenant-Id"));
            configuration.setAllowCredentials(true);
            var source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }
        @Bean java.security.KeyPair r9Keys() throws Exception {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        }
        @Bean RSAPublicKey r9Public(java.security.KeyPair r9Keys) { return (RSAPublicKey) r9Keys.getPublic(); }
        @Bean RSAPrivateKey r9Private(java.security.KeyPair r9Keys) { return (RSAPrivateKey) r9Keys.getPrivate(); }
        @Bean CommandLineRunner r9Users(UserAccountRepository users, PasswordEncoder encoder) {
            return args -> {
                var tenant = UUID.fromString("11111111-1111-4111-8111-111111111111");
                String encoded = encoder.encode("Synthetic-R9-Only!");
                for (int index = 1; index <= 2; index++) {
                    users.saveAndFlush(new UserAccount(UUID.fromString(
                        "90000000-0000-4000-8000-00000000000" + index), tenant,
                        "synthetic" + index + "@example.invalid", encoded,
                        true, false, null, Instant.now(), null));
                }
            };
        }
    }
}
