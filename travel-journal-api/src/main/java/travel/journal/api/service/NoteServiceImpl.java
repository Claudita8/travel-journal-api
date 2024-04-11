package travel.journal.api.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import travel.journal.api.dto.travelJournal.outbound.NoteDetailsDTO;
import travel.journal.api.entities.Files;
import travel.journal.api.entities.Note;
import travel.journal.api.exception.NoPermissionException;
import travel.journal.api.exception.ResourceNotFoundException;
import travel.journal.api.repositories.FilesRepository;
import travel.journal.api.repositories.NoteRepository;
import travel.journal.api.security.services.UserDetailsImpl;

import java.time.LocalDate;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final FilesRepository fileRepository;

    private final ModelMapper modelMapper;

    public NoteServiceImpl(NoteRepository noteRepository, FilesRepository fileRepository, ModelMapper modelMapper) {
        this.noteRepository = noteRepository;
        this.fileRepository = fileRepository;
        this.modelMapper = modelMapper;
    }

    public NoteDetailsDTO getNoteDetails(int travelId, int noteId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Integer userId;
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        userId = userDetails.getId();

        Note note = noteRepository.findByTravelJournal_User_UserIdAndTravelJournal_TravelIdAndNoteId(userId,travelId,noteId);
        if (note == null)
            throw new ResourceNotFoundException("");

        List<Files> filesList = fileRepository.findByNoteId(noteId);

        NoteDetailsDTO noteDetailsDTO = modelMapper.map(note, NoteDetailsDTO.class);
        noteDetailsDTO.setDate(getFormattedDate(note.getDate()));
        noteDetailsDTO.setFilesList(filesList);

        return noteDetailsDTO;
    }

    private String getFormattedDate(LocalDate date){
        return String.format("%s/%s/%s",date.getDayOfMonth(), date.getMonthValue(),date.getYear());
    }
}
