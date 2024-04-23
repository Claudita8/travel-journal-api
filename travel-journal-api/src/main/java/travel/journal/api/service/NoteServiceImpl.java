package travel.journal.api.service;


import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.CreateNoteDTO;
import travel.journal.api.dto.travelJournal.outbound.NoteDetailsDTO;
import travel.journal.api.entities.Files;
import travel.journal.api.entities.Note;
import travel.journal.api.entities.TravelJournal;
import travel.journal.api.entities.User;
import travel.journal.api.exception.BadRequestException;
import travel.journal.api.exception.ResourceNotFoundException;
import travel.journal.api.repositories.FilesRepository;
import travel.journal.api.repositories.NoteRepository;
import travel.journal.api.security.services.UserDetailsImpl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService  {
    private final TravelService travelService;
    private final FilesServiceImpl filesService;
    private final ModelMapper modelMapper;
    private final NoteRepository noteRepository;
    private final FilesRepository filesRepository;
    private final UserService userService;

    public NoteServiceImpl(TravelService travelService, FilesServiceImpl filesService, NoteRepository noteRepository, FilesRepository filesRepository, UserService userService, ModelMapper modelMapper) {
        this.travelService = travelService;
        this.filesService = filesService;
        this.noteRepository = noteRepository;
        this.filesRepository = filesRepository;
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
                filesRepository.deleteAll(noteToDelete.getPhotos());
                noteRepository.delete(noteToDelete);
            } else {
                throw new ResourceNotFoundException("Note with id: " + id + " does not exist");
            }
        } else {
            throw new ResourceNotFoundException("Note with id: " + id + " does not exist");
        }
    }

    public void save(int id, CreateNoteDTO createNoteDTO, List<MultipartFile> photos ) throws IOException {
        Optional<User> user = userService.getCurrentUser();
        TravelJournal travelJournal = travelService.findByUserUserIdAndTravelId(user.get().getUserId(), id);

        if(travelJournal == null){
            throw new ResourceNotFoundException("");
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

        if(createNoteDTO.getDescription().length() >= 250){
            throw new BadRequestException("The maximum limit for description is 250 characters.");
        }

        Note note = new Note();
        note.setDate(getParsedDate(createNoteDTO.getDate()));
        note.setDescription(createNoteDTO.getDescription());
        note.setDestinationName(createNoteDTO.getDestinationName());
        note.setTravelJournal(travelJournal);
        List<Files> files = new ArrayList<>();
        for(MultipartFile photo:photos){
            Files file = filesService.CheckAndSaveImage(photo);
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

        List<Files> filesList = note.getPhotos();
        NoteDetailsDTO noteDetailsDTO = modelMapper.map(note, NoteDetailsDTO.class);
        noteDetailsDTO.setDate(getFormattedDate(note.getDate()));
        noteDetailsDTO.setFilesList(filesList);
        return noteDetailsDTO;
    }

    private String getFormattedDate(LocalDate date){
        return String.format("%s/%s/%s",date.getDayOfMonth(), date.getMonthValue(),date.getYear());
    }

    public void editNote(int travelJournalId, int noteId, CreateNoteDTO createNoteDTO, List<MultipartFile> photos) throws IOException {
        TravelJournal travelJournal = travelService.getTravelJournalById(travelJournalId);
        Optional<User> user = userService.getCurrentUser();
        Optional<Note> optionalNote = noteRepository.findById(noteId);

        if (!optionalNote.isPresent() || user.get().getUserId()!=travelJournal.getUser().getUserId()) {
            throw new ResourceNotFoundException("");
        }

        if (createNoteDTO.getDestinationName() == null || createNoteDTO.getDestinationName().isEmpty()) {
            throw new BadRequestException("Destination name is required.");
        }

        if(photos.size() == 1){
            MultipartFile photo = photos.get(0);
            byte[] check=photo.getBytes();
            if(check.length == 0) {
                throw new BadRequestException("At least one photo must be uploaded.");
            }
        }

        if (photos.size() >= 8) {
            throw new BadRequestException("A maximum of 7 photos can be uploaded.");
        }

        if (createNoteDTO.getDestinationName().length() >= 250) {
            throw new BadRequestException("Destination name should be maximum 250 characters.");
        }

        if (createNoteDTO.getDescription().length() >= 250) {
            throw new BadRequestException("Description should be maximum 250 characters.");
        }

        if(checkDateIsInTravelJournalDateInterval(getParsedDate(createNoteDTO.getDate()),travelJournal)){
            throw new BadRequestException("The specified date is outside the range of the travel journal.");
        }

        Note note = optionalNote.get();
        note.setDate(getParsedDate(createNoteDTO.getDate()));
        note.setDescription(createNoteDTO.getDescription());
        note.setDestinationName(createNoteDTO.getDestinationName());

        List<Files> photosToDelete = photosToErase(note.getPhotos(), photos);
        for(var files : photosToDelete){
            note.getPhotos().remove(files);
        }
        filesRepository.deleteAll(photosToDelete);

        List<Files> photosToKeep = photosToSave(photos);
        note.setPhotos(photosToKeep);
        save(note);
    }
    @Override
    public List<Files> photosToErase(List<Files> notePhotos, List<MultipartFile> photosToEdit){
        List<Files> photosToDelete = new ArrayList<>();
        List<String> existingPhotoNames = new ArrayList<>();

        for (var photo : photosToEdit) {
            existingPhotoNames.add(photo.getOriginalFilename());
        }

        for (var photo : notePhotos) {
            if (!existingPhotoNames.contains(photo.getFileName())) {
                photosToDelete.add(photo);
            }
        }
        return  photosToDelete;
    }
    @Override
    public List<Files>  photosToSave(List<MultipartFile> photos) throws IOException {
        List<Files> photosToKeep = new ArrayList<>();
        for(MultipartFile photo:photos){
            Files file = filesService.CheckAndSaveImage(photo);
            photosToKeep.add(file);
        }
        return photosToKeep;
    }
}
