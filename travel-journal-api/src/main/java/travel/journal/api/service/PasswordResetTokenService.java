package travel.journal.api.service;

import travel.journal.api.entities.PasswordResetToken;
import travel.journal.api.entities.User;

import java.time.LocalDateTime;

public interface PasswordResetTokenService {
    public String sendEmail(User user);
    public String generateResetToken(User user);

    public boolean hasExpired(LocalDateTime expiryDateTime);
    public boolean canGenerateNewResetToken(User user);
    void saveToken(PasswordResetToken passwordResetToken);
    PasswordResetToken saveTokenAndGet(PasswordResetToken passwordResetToken);
    PasswordResetToken findByToken(String token);
    boolean hasValidResetToken(PasswordResetToken passwordResetToken);

    void deleteExpiredOrUsedResetToken();
}
