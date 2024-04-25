package travel.journal.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import travel.journal.api.entities.File;

@Repository

public interface FileRepository extends JpaRepository<File, Integer >{
    File findByFileName(String fileName);
}
