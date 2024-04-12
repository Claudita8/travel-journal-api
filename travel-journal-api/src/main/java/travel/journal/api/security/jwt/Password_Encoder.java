package travel.journal.api.security.jwt;

import org.springframework.security.crypto.password.PasswordEncoder;

public class Password_Encoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return null;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
    }
}
