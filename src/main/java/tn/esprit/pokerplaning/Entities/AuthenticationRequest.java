package tn.esprit.pokerplaning.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CrossOrigin

public class AuthenticationRequest {

    private String email;
    String  password;
    private String recaptchaResponse;


}
