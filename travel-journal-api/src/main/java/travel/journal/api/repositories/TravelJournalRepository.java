package travel.journal.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import travel.journal.api.entities.TravelJournal;

import java.util.List;

@Repository

public interface TravelJournalRepository extends JpaRepository<TravelJournal, Integer> {
    List<TravelJournal> findByUserUserIdOrderByStartDateDesc(int userId);
}
