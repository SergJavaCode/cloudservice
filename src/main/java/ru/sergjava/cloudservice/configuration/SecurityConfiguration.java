package ru.sergjava.cloudservice.configuration;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.sergjava.cloudservice.service.TokenLogoutHandler;

import javax.sql.DataSource;


@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableMethodSecurity(
        securedEnabled = true,
        prePostEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfiguration implements WebMvcConfigurer {

    private TokenLogoutHandler tokenLogoutHandler;

    public SecurityConfiguration(TokenLogoutHandler tokenLogoutHandler) {
        this.tokenLogoutHandler = tokenLogoutHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        return users;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, InitialAuthenticationFilter initialAuthenticationFilter, ValidationTokenFilter validationTokenFilter) throws Exception {
        http.addFilterAt(initialAuthenticationFilter, BasicAuthenticationFilter.class);
        http.addFilterAfter(validationTokenFilter, InitialAuthenticationFilter.class);
        http.csrf(AbstractHttpConfigurer::disable);
        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.ALL));
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                )
                .logout((logout) ->
                        logout
                                .addLogoutHandler(clearSiteData)
                                .addLogoutHandler(tokenLogoutHandler)
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                )
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins("http://localhost:8081")
                .allowedMethods("*");
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("128MB"));
        factory.setMaxRequestSize(DataSize.parse("128MB"));
        return factory.createMultipartConfig();
    }

}
