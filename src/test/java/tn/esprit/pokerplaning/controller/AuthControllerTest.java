package tn.esprit.pokerplaning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tn.esprit.pokerplaning.Entities.AuthenticationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.pokerplaning.Entities.Role;
import tn.esprit.pokerplaning.Entities.User;
import tn.esprit.pokerplaning.Repositories.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = tn.esprit.pokerplaning.LoginMicroserviceApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EnableAutoConfiguration
@EntityScan(basePackages = "tn.esprit.pokerplaning.Entities")
@TestPropertySource("classpath:application-test.properties")
public class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupTestUser() {
        userRepository.deleteAll(); // Clean slate
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setBanned(false);
        user.setRole(Role.Developpeur);
        userRepository.save(user);
    }

    @Test
    public void testLoginWithValidCredentials_returns200() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password", null);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testLoginWithInvalidCredentials_returns403() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("wrong@example.com", "badpass", null);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}