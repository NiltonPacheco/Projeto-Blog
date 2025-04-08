package nilton.acelera.demo.security;

import nilton.acelera.demo.security.jwt.TokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class ConfiguracaoDeSeguranca {

    private final TokenFilter tokenFilter;

    public ConfiguracaoDeSeguranca(TokenFilter tokenFilter) {
        this.tokenFilter = tokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/usuarios/login", "/usuarios/cadastrar").permitAll()  // Permitir login e cadastro
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()  // Permitir Swagger
                .requestMatchers(HttpMethod.POST, "/postagens/**").hasRole("ADMIN")  // Apenas usuários com ROLE_ADMIN podem acessar POST de /postagens
                .anyRequest().authenticated()  
            )
            .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);  // Adiciona o TokenFilter antes do UsernamePasswordAuthenticationFilter

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Criptografia para senhas
    }
}
