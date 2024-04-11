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

        Note note = noteRepository.findById(noteId).orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + noteId));
        if (note.getTravelJournal().getTravelId() != travelId || note.getTravelJournal().getUser().getUserId() != userId)
            throw new NoPermissionException("No permission for this note");

        List<Files> filesList = fileRepository.findByNoteId(noteId);

        NoteDetailsDTO noteDetailsDTO = modelMapper.map(note, NoteDetailsDTO.class);
        noteDetailsDTO.setFilesList(filesList);

        return noteDetailsDTO;
    }
}
