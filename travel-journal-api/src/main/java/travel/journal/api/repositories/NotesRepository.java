package travel.journal.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import travel.journal.api.entities.Note;

@Repository
@EnableJpaRepositories
public interface NotesRepository extends JpaRepository<Note, Integer> {

}
