package travel.journal.api.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import travel.journal.api.entities.PasswordResetToken;

import java.time.LocalDateTime;

public interface TokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    PasswordResetToken findByToken(String token);
    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.used = true OR t.expiryDateTime <= :currentDateTime")
    void deleteByUsedTrueOrExpiryDateTimeBefore(LocalDateTime currentDateTime);

}