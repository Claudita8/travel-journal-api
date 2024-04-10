package travel.journal.api.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import travel.journal.api.service.NoteService;


@RestController
@Validated
@RequestMapping("/api/note")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/deleteNote/{id}")
    ResponseEntity<Void> deleteTravel(@PathVariable("id") int noteId) {
        noteService.deleteNote(noteId);
        return ResponseEntity.noContent().build();
    }
}