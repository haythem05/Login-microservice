package tn.esprit.pokerplaning.service;

import tn.esprit.pokerplaning.Entities.User.*;
import tn.esprit.pokerplaning.Repositories.User.UserRepository;
import tn.esprit.pokerplaning.Services.User.AuthenticationService;
import tn.esprit.pokerplaning.Services.User.JwtService;
import tn.esprit.pokerplaning.Services.User.UserServices;
import tn.esprit.pokerplaning.Services.User.twilio.SmsService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private UserServices userServices;
    @Mock private SmsService smsService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    public AuthenticationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticate_SuccessfulLogin() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password", null);
        User user = new User();
        user.setEmail("test@example.com");
        user.setBanned(false);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mockToken");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals(user, response.getUser());
    }

    @Test
    public void testAuthenticate_BannedUser() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("banned@example.com", "password", null);
        User bannedUser = new User();
        bannedUser.setEmail("banned@example.com");
        bannedUser.setBanned(true);
        bannedUser.setPhone("123456");

        when(userRepository.findByEmail("banned@example.com")).thenReturn(Optional.of(bannedUser));

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertEquals("Your account is banned", response.getMessage());
        assertNull(response.getToken());
    }
    @Test
    void testRegisterSuccess() throws IOException {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .gender(Gender.Male)
                .phone("123456789")
                .skillRate(5)
                .role(Role.Developpeur)
                .build();

        User mockUser = User.builder()
                .userId(1L)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password("encodedPassword")
                .gender(request.getGender())
                .phone(request.getPhone())
                .skillRate(request.getSkillRate())
                .role(request.getRole())
                .build();

        // Mock behavior
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mocked-jwt-token");

        // Act
        AuthenticationResponse response = authenticationService.register(request, null);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals(mockUser.getEmail(), response.getUser().getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }




}