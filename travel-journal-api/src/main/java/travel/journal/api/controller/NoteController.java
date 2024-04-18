package travel.journal.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.journal.api.dto.travelJournal.outbound.NoteDetailsDTO;
import travel.journal.api.service.NoteService;
import travel.journal.api.service.NoteServiceImpl;

@RestController
@RequestMapping("/travel-journal")
public class NoteController {
    private final NoteServiceImpl noteService;

    public NoteController(NoteServiceImpl noteService) {
        this.noteService = noteService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/travel/{travelId}/view-note/{noteId}")
    public NoteDetailsDTO getNoteDetails(@PathVariable int travelId, @PathVariable int noteId) {
        return noteService.getNoteDetails(travelId, noteId);
    }
}
