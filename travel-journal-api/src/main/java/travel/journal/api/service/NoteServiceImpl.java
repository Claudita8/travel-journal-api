package travel.journal.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.CreateNoteDTO;
import travel.journal.api.entities.Files;
import travel.journal.api.entities.Note;
import travel.journal.api.entities.TravelJournal;
import travel.journal.api.exception.BadRequestException;
import travel.journal.api.repositories.FilesRepository;
import travel.journal.api.repositories.NoteRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService  {
    private final TravelService travelService;

    private final FilesRepository filesRepository;

    private final FilesServiceImpl filesService;
    private final NoteRepository noteRepository;

    public NoteServiceImpl(TravelService travelService, FilesRepository filesRepository, FilesServiceImpl filesService, NoteRepository noteRepository) {
        this.travelService = travelService;
        this.filesRepository = filesRepository;
        this.filesService = filesService;
        this.noteRepository = noteRepository;
    }

    @Override
    public boolean save(int id, CreateNoteDTO createNoteDTO, List<MultipartFile> photos ) throws IOException {
        TravelJournal travelJournal=travelService.getTravelJourbalById(id);
        if(travelJournal==null){
          throw new BadRequestException("Travel with id: " + id + " does not exist");
        }
        if(photos.size()==1){
            MultipartFile photo= photos.get(0);
            byte[] check=photo.getBytes();
            System.out.println(check.length);
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
