package travel.journal.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.CreateNoteDTO;
import travel.journal.api.entities.Files;
import travel.journal.api.entities.Note;
import travel.journal.api.entities.TravelJournal;
import travel.journal.api.entities.User;
import travel.journal.api.exception.BadRequestException;
import travel.journal.api.exception.NoPermissionException;
import travel.journal.api.exception.ResourceNotFoundException;
import travel.journal.api.repositories.NoteRepository;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService  {
    private final TravelService travelService;

    private final FilesServiceImpl filesService;

    private final NoteRepository noteRepository;

    private final UserService userService;

    public NoteServiceImpl(TravelService travelService, FilesServiceImpl filesService, NoteRepository noteRepository, UserService userService) {
        this.travelService = travelService;
        this.filesService = filesService;
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    @Override
    public void save(int id, CreateNoteDTO createNoteDTO, List<MultipartFile> photos ) throws IOException {
        TravelJournal travelJournal=travelService.getTravelJournalById(id);
        Optional<User> user = userService.getCurrentUser();
        if(user.isPresent()) {
            User getuser = user.get();
            if (travelJournal.getUser() != getuser) {
                throw new ResourceNotFoundException("");
            }
        }
        if(travelJournal == null){
          throw new BadRequestException("Travel with id: " + id + " does not exist");
        }
        if(photos.size() == 1){
            MultipartFile photo= photos.get(0);
            byte[] check=photo.getBytes();
            if(check.length == 0) {
                throw new BadRequestException("At least one photo must be uploaded.");
            }
        }
        if(photos.size() >= 8){
            throw new BadRequestException("You can upload a maximum of 7 photos.");
        }
        if(checkDateIsInTravelJournalDateInterval(getParsedDate(createNoteDTO.getDate()),travelJournal)){
            throw new BadRequestException("The specified date is outside the range of the travel journal.");
        }
        if(createNoteDTO.getDescription().length() > 250){
            throw new BadRequestException("The maximum limit for description is 250 characters.");
        }

        Note note=new Note();
        note.setDate(getParsedDate(createNoteDTO.getDate()));
        note.setDescription(createNoteDTO.getDescription());
        note.setDestinationName(createNoteDTO.getDestinationName());
        note.setTravelJournal(travelJournal);

        List<Files> files = new ArrayList<>();
        for(MultipartFile photo:photos){

            Files file=filesService.ChechAndSaveImage(photo);
            files.add(file);
        }
        note.setPhotos(files);
        save(note);
    }

    @Override
    public void save(Note note) {
         noteRepository.save(note);
    }

    @Override
    public LocalDate getParsedDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        return LocalDate.parse(date, formatter);
    }
    public boolean checkDateIsInTravelJournalDateInterval(LocalDate date, TravelJournal travelJournal){
        return travelJournal.getStartDate().isAfter(date)||travelJournal.getEndDate().isBefore(date);
    }

    @Override
    public Note saveNoteAndGet(Note note) {
         return noteRepository.save(note);
    }

}
