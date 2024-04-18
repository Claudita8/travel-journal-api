package travel.journal.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import travel.journal.api.entities.Files;

import java.util.List;

@Repository

public interface FilesRepository extends JpaRepository<Files, Integer >{
    Files findByFileName(String fileName);
//    @Query("SELECT f FROM Files f JOIN f.notesList nf WHERE nf.noteId = :noteId")
//    List<Files> findByNoteId(Integer noteId);
}
