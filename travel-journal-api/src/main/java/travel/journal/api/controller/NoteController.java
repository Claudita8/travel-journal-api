package travel.journal.api.controller;

<<<<<<< HEAD

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
=======
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.CreateNoteDTO;

import travel.journal.api.service.NoteServiceImpl;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/note")
public class NoteController {

    private final NoteServiceImpl noteService;

    public NoteController(NoteServiceImpl noteService) {
>>>>>>> dev-craiova
        this.noteService = noteService;
    }

    @PreAuthorize("isAuthenticated()")
<<<<<<< HEAD
    @DeleteMapping("/deleteNote/{id}")
    ResponseEntity<Void> deleteTravel(@PathVariable("id") int noteId) {
        noteService.deleteNote(noteId);
        return ResponseEntity.noContent().build();
    }
}
=======
    @PostMapping("/saveNote/{traveljournalid}")
    public ResponseEntity<?> saveNote(@PathVariable("traveljournalid") Integer id, @Valid @RequestPart("CreateNoteDTO") CreateNoteDTO createNoteDTO, @RequestParam("files") List<MultipartFile> files) throws IOException {
        noteService.save(id, createNoteDTO, files);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
>>>>>>> dev-craiova
