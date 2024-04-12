package travel.journal.api.service;

import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.CreateNoteDTO;
import travel.journal.api.dto.travelJournal.outbound.NoteDetailsDTO;
import travel.journal.api.entities.Note;
import travel.journal.api.entities.TravelJournal;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface NoteService {
    void save(int id, CreateNoteDTO createNoteDTO, List<MultipartFile> photos) throws IOException;

    void save(Note note);

    LocalDate getParsedDate(String date);

    boolean checkDateIsInTravelJournalDateInterval(LocalDate date, TravelJournal travelJournal);
}
