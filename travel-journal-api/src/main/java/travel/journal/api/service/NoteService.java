package travel.journal.api.service;

import travel.journal.api.dto.travelJournal.outbound.NoteDetailsDTO;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.CreateNoteDTO;
import travel.journal.api.entities.File;
import travel.journal.api.entities.Note;
import travel.journal.api.entities.TravelJournal;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface NoteService {
    NoteDetailsDTO getNoteDetails(int travelId, int noteId);

    void save(int id, CreateNoteDTO createNoteDTO, List<MultipartFile> photos) throws IOException;

    void save(Note note);

    LocalDate getParsedDate(String date);

    boolean checkDateIsInTravelJournalDateInterval(LocalDate date, TravelJournal travelJournal);

    void deleteNote(Integer id);

    List<File> photoToErase(List<File> notePhotos, List<MultipartFile> photosToEdit);

    List<File>  photoToSave(List<MultipartFile> photos) throws IOException;
}
