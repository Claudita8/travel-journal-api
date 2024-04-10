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
import travel.journal.api.repositories.NoteRepository;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    public boolean save(int id, CreateNoteDTO createNoteDTO, List<MultipartFile> photos ) throws IOException {
        TravelJournal travelJournal=travelService.getTravelJourbalById(id);
        Optional<User> user = userService.getCurrentUser();
        if(user.isPresent()) {
            User getuser=user.get();
            if (travelJournal.getUser() != getuser) {
                throw new NoPermissionException("You aren't an owner of travel journal card.");
            }
        }
        if(travelJournal==null){
          throw new BadRequestException("Travel with id: " + id + " does not exist");
        }
        if(photos.size()==1){
            MultipartFile photo= photos.get(0);
            byte[] check=photo.getBytes();
            if(check.length==0) {
                throw new BadRequestException("Trebuie incarcat cel putin o poza.");
            }
        }
        if(photos.size()>=8){
            throw new BadRequestException("Poti incarca maxim 7 poze.");
        }
        if(travelJournal.getStartDate().isAfter(createNoteDTO.getParsedDate())||travelJournal.getEndDate().isBefore(createNoteDTO.getParsedDate())){
            throw new BadRequestException("Data specificată este în afara intervalului de la jurnalul de călătorie.");
        }
        if(createNoteDTO.getDescription().length()>250){
            throw new BadRequestException("Limita maxima pentru descriere este de 250 de caractere");
        }

        Note note=new Note();
        note.setDate(createNoteDTO.getParsedDate());
        note.setDescription(createNoteDTO.getDescription());
        note.setDestinationName(createNoteDTO.getDestinationName());
        note.setTravelJournal(travelJournal);


        List<Files> files=new ArrayList<>();

        for(MultipartFile photo:photos){
            Files file=filesService.saveImage(photo);

           files.add(file);

        }
        note.setPhotos(files);
        save(note);

        return true;

    }

    @Override
    public void save(Note note) {
         noteRepository.save(note);
    }

    @Override
    public Note saveNoteAndGet(Note note) {
         return noteRepository.save(note);
    }

}
