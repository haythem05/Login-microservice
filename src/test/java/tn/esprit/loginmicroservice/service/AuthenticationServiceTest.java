package tn.esprit.loginmicroservice.service;

import tn.esprit.pokerplaning.Entities.User.AuthenticationRequest;
import tn.esprit.pokerplaning.Entities.User.AuthenticationResponse;
import tn.esprit.pokerplaning.Entities.User.User;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}