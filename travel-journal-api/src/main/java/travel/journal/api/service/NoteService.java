package travel.journal.api.service;

import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.CreateNoteDTO;
import travel.journal.api.dto.travelJournal.outbound.NoteDetailsDTO;
import travel.journal.api.entities.Note;

import java.io.IOException;
import java.util.List;

public interface NoteService {
    boolean save(int id, CreateNoteDTO createNoteDTO, List<MultipartFile> photos) throws IOException;
    Note saveNoteAndGet(Note note);
    void save(Note note);
}
