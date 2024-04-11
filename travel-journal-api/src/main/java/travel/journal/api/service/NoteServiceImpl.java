package travel.journal.api.service;

import org.springframework.stereotype.Service;
import travel.journal.api.entities.Note;
import travel.journal.api.entities.User;
import travel.journal.api.exception.ResourceNotFoundException;
import travel.journal.api.repositories.FilesRepository;
import travel.journal.api.repositories.NotesRepository;

import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService{
    private final NotesRepository notesRepository;
    private final FilesRepository filesRepository;
    private final UserService userService;

    public NoteServiceImpl(NotesRepository notesRepository, FilesRepository filesRepository, UserService userService) {
        this.notesRepository = notesRepository;
        this.filesRepository = filesRepository;
        this.userService = userService;
    }

    @Override
    public void deleteNote(Integer id) {
        Optional<User> optionalUser = userService.getCurrentUser();
        Optional<Note> optionalNote = notesRepository.findById(id);

        if(optionalNote.isPresent() && optionalUser.isPresent()) {
                User currentUser = optionalUser.get();
                if(currentUser.equals(optionalNote.get().getTravelJournal().getUser())) {
                    Note noteToDelete = optionalNote.get();
                    filesRepository.deleteAll(noteToDelete.getFilesList());
                    notesRepository.delete(noteToDelete);
                } else {
                    throw new ResourceNotFoundException("Note with id: " + id + " does not exist");
                }
        } else {
            throw new ResourceNotFoundException("Note with id: " + id + " does not exist");
        }
    }
}
