package travel.journal.api.controller;

import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.CreateNoteDTO;

import travel.journal.api.entities.Note;
import travel.journal.api.repositories.NoteRepository;
import travel.journal.api.service.NoteServiceImpl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/note")
public class NoteController {

    private final NoteServiceImpl noteService;
    private final NoteRepository noteRepository;

    public NoteController(NoteServiceImpl noteService, NoteRepository noteRepository) {
        this.noteService = noteService;
        this.noteRepository = noteRepository;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/saveNote/{traveljournalid}")
    public ResponseEntity<?> saveNote(@PathVariable("traveljournalid") Integer id, @RequestPart("CreateNoteDTO") CreateNoteDTO createNoteDTO, @RequestParam("files") List<MultipartFile> files) throws IOException {
        noteService.save(id, createNoteDTO, files);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
