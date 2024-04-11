package travel.journal.api.service;

import travel.journal.api.entities.PasswordResetToken;
import travel.journal.api.entities.User;

import java.time.LocalDateTime;

public interface PasswordResetTokenService {
    boolean sendEmail(User user);

    PasswordResetToken generateResetToken(User user);

    boolean hasExpired(LocalDateTime expiryDateTime);

    boolean canGenerateNewResetToken(User user);

    void saveToken(PasswordResetToken passwordResetToken);

    PasswordResetToken findByToken(String token);

    boolean hasValidResetToken(PasswordResetToken passwordResetToken);

    boolean isTokenUsedOrDateExpired(PasswordResetToken passwordResetToken);

}
