package travel.journal.api.service;


import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.CreateNoteDTO;
import travel.journal.api.dto.travelJournal.outbound.NoteDetailsDTO;
import travel.journal.api.entities.File;
import travel.journal.api.entities.Note;
import travel.journal.api.entities.TravelJournal;
import travel.journal.api.entities.User;
import travel.journal.api.exception.BadRequestException;
import travel.journal.api.exception.ResourceNotFoundException;
import travel.journal.api.repositories.FileRepository;
import travel.journal.api.repositories.NoteRepository;
import travel.journal.api.security.services.UserDetailsImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService  {
    private final TravelService travelService;
    private final FileServiceImpl filesService;
    private final ModelMapper modelMapper;
    private final NoteRepository noteRepository;
    private final FileRepository fileRepository;

    private final UserService userService;

    public NoteServiceImpl(TravelService travelService, FileServiceImpl filesService, NoteRepository noteRepository, FileRepository fileRepository, UserService userService, ModelMapper modelMapper) {
        this.travelService = travelService;
        this.filesService = filesService;
        this.noteRepository = noteRepository;
        this.fileRepository = fileRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void deleteNote(Integer id) {
        Optional<User> optionalUser = userService.getCurrentUser();
        Optional<Note> optionalNote = noteRepository.findById(id);

        if (optionalNote.isPresent() && optionalUser.isPresent()) {
            User currentUser = optionalUser.get();
            if (currentUser.equals(optionalNote.get().getTravelJournal().getUser())) {
                Note noteToDelete = optionalNote.get();
                fileRepository.deleteAll(noteToDelete.getPhotos());
                noteRepository.delete(noteToDelete);
            } else {
                throw new ResourceNotFoundException("Note with id: " + id + " does not exist");
            }
        } else {
            throw new ResourceNotFoundException("Note with id: " + id + " does not exist");
        }
    }

    public void save(int id, CreateNoteDTO createNoteDTO, List<MultipartFile> photos ) throws IOException {
        TravelJournal travelJournal = travelService.getTravelJournalById(id);
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
            MultipartFile photo = photos.get(0);
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

        Note note = new Note();
        note.setDate(getParsedDate(createNoteDTO.getDate()));
        note.setDescription(createNoteDTO.getDescription());
        note.setDestinationName(createNoteDTO.getDestinationName());
        note.setTravelJournal(travelJournal);

        List<File> files = new ArrayList<>();
        for(MultipartFile photo:photos){
            File file = filesService.CheckAndSaveImage(photo);
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

    public NoteDetailsDTO getNoteDetails(int travelId, int noteId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Integer userId;
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        userId = userDetails.getId();

        Note note = noteRepository.findByTravelJournal_User_UserIdAndTravelJournal_TravelIdAndNoteId(userId,travelId,noteId);
        if (note == null)
            throw new ResourceNotFoundException("");

        List<File> fileList = note.getPhotos();

        NoteDetailsDTO noteDetailsDTO = modelMapper.map(note, NoteDetailsDTO.class);
        noteDetailsDTO.setDate(getFormattedDate(note.getDate()));
        noteDetailsDTO.setFileList(fileList);

        return noteDetailsDTO;
    }

    private String getFormattedDate(LocalDate date){
        return String.format("%s/%s/%s",date.getDayOfMonth(), date.getMonthValue(),date.getYear());
    }
}
