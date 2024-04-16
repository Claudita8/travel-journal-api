package travel.journal.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import travel.journal.api.entities.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
    Note findByTravelJournal_User_UserIdAndTravelJournal_TravelIdAndNoteId(int userId, int travelId, int noteId);
}
