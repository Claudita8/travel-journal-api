
package travel.journal.api.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.travelJournal.inbound.TravelJournalDTO;
import travel.journal.api.dto.travelJournal.outbound.CardTravelJournalDTO;
import travel.journal.api.dto.travelJournal.outbound.NoteEntryDTO;
import travel.journal.api.dto.travelJournal.outbound.TravelJournalDetailsDTO;
import travel.journal.api.entities.Files;
import travel.journal.api.entities.TravelJournal;
import travel.journal.api.entities.User;
import travel.journal.api.exception.*;
import travel.journal.api.repositories.TravelJournalRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TravelServiceImpl implements TravelService {

    private final TravelJournalRepository travelRepository;
    private final FilesServiceImpl filesService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public TravelServiceImpl(TravelJournalRepository travelRepository, FilesServiceImpl filesService, UserService userService, ModelMapper modelMapper) {
        this.travelRepository = travelRepository;
        this.filesService = filesService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.modelMapper.addMappings(new PropertyMap<TravelJournal, TravelJournalDetailsDTO>() {
            @Override
            protected void configure() {
                map().setCoverPhoto(source.getCoverPhoto());
            }
        });
    }

    @Override
    public TravelJournalDetailsDTO createTravelJournal(TravelJournalDTO travelJournalDTO, MultipartFile file) throws IOException {
        TravelJournal travelToCreate = modelMapper.map(travelJournalDTO, TravelJournal.class);

        Optional<User> user = userService.getCurrentUser();
        User currentUser = null;

        if (user.isPresent()) {
            currentUser = user.get();
        }

        if (travelRepository.existsByLocationAndUser(travelToCreate.getLocation(), currentUser)) {
            throw new DuplicateTravelNameException("A card with the same name already exists");
        }

        if(!travelJournalDTO.getStartDate().isBefore(travelJournalDTO.getEndDate())){
            throw new InvalidDateRangeException("Start date must be before end date of the travel journal");
        }

        Files createdFile = filesService.saveImage(file);
        travelToCreate.setCoverPhoto(createdFile);
        travelToCreate.setHasCoverPhoto(createdFile.getFileContent().length > 0);

        TravelJournal createdTravel = travelRepository.save(travelToCreate);

        return modelMapper.map(createdTravel, TravelJournalDetailsDTO.class);
    }

    @Override
    public TravelJournalDetailsDTO getTravelJournal(Integer id) {
        TravelJournal travel = travelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Travel with id: " + id + " does not exist"));
        return modelMapper.map(travel, TravelJournalDetailsDTO.class);
    }

    @Override
    public TravelJournalDetailsDTO getTravelJournal(Integer id, Integer userId) {
        TravelJournal travel = travelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Travel with id: " + id + " does not exist"));
        if (travel.getUser().getUserId() == userId) {
            TravelJournalDetailsDTO travelJournalDetailsDTO = modelMapper.map(travel, TravelJournalDetailsDTO.class);
            List<NoteEntryDTO> noteEntryDTOList = travel.getNoteList().stream()
                    .map(note -> modelMapper.map(note, NoteEntryDTO.class))
                    .collect(Collectors.toList());
            travelJournalDetailsDTO.setNotesList(noteEntryDTOList);
            travelJournalDetailsDTO.setNotesNumber(noteEntryDTOList.size());
            return travelJournalDetailsDTO;
        }
        throw new NoPermissionException("No permission for travel with id: " + id);
    }

    @Override
    public List<TravelJournalDetailsDTO> getAllTravelJournals() {
        List<TravelJournal> allTravels = travelRepository.findAll();
        return allTravels.stream().map(travelJournal -> modelMapper.map(travelJournal, TravelJournalDetailsDTO.class)).collect(Collectors.toList());
    }

    @Override
    public TravelJournalDetailsDTO modifyTravelJournal(Integer id, TravelJournalDTO travelJournalDTO, MultipartFile file) throws IOException {
        Optional<User> user = userService.getCurrentUser();

        User checkuser = null;

        if (user.isPresent()) {
            checkuser = user.get();
        }

        Optional<TravelJournal> existingTravelOptional = travelRepository.findById(id);

        if (existingTravelOptional.isPresent()) {
            if (checkuser != existingTravelOptional.get().getUser()) {
                throw new UnauthorizedAccesException("Current user is not authorized to update this travel journal");
            }
            TravelJournal existingTravel = existingTravelOptional.get();

            boolean isLocationChanged = !Objects.equals(existingTravel.getLocation(), travelJournalDTO.getLocation());

            if (isLocationChanged && travelRepository.existsByLocationAndUser(travelJournalDTO.getLocation(), checkuser)) {
                throw new DuplicateTravelNameException("A card with the same name already exists fot the current user");
            }

            if(!travelJournalDTO.getStartDate().isBefore(travelJournalDTO.getEndDate())){
                throw new InvalidDateRangeException("Start date must be before end date of the travel");
            }

            Files modifiedImage = filesService.modifyImage(existingTravel.getCoverPhoto().getFileId(), file);

            existingTravel.setHasCoverPhoto(modifiedImage.getFileContent().length > 0);
            existingTravel.setCoverPhoto(modifiedImage);
            existingTravel.setBudget(travelJournalDTO.getBudget());
            existingTravel.setDescription(travelJournalDTO.getDescription());
            existingTravel.setLocation(travelJournalDTO.getLocation());
            existingTravel.setEndDate(travelJournalDTO.getEndDate());
            existingTravel.setStartDate(travelJournalDTO.getStartDate());
            existingTravel.setHasCoverPhoto(existingTravel.getHasCoverPhoto());
            existingTravel.setNotesList(existingTravel.getNoteList());

            TravelJournal modifiedTravel = travelRepository.save(existingTravel);

            return modelMapper.map(modifiedTravel, TravelJournalDetailsDTO.class);
        } else {
            throw new ResourceNotFoundException("Travel with id: " + id + " does not exist");
        }
    }
    @Override
    public void deleteTravelJournal(Integer id) {
        Optional<User> user = userService.getCurrentUser();
        User checkuser = null;
        if (user.isPresent()) {
            checkuser = user.get();
        }
        Optional<TravelJournal> travelOptional = travelRepository.findById(id);
        if (travelOptional.isPresent()) {
            if (user.isPresent()) {
                if (checkuser.equals(travelOptional.get().getUser())) {
                    travelRepository.deleteById(id);

                } else {
                    throw new UnauthorizedAccesException("Current user is not authorized to delete this travel journal");
                }
            }

        } else {
            throw new ResourceNotFoundException("Travel with id: " + id + " does not exist");
        }
    }


    @Override
    public List<CardTravelJournalDTO> getUserTravelJournals() {
        User user = userService.getCurrentUser().orElseThrow(() -> new UnauthorizedAccesException("Current user is not authorized to get this travel journal"));

        List<TravelJournal> userTravels = travelRepository.findByUserUserIdOrderByStartDateDesc(user.getUserId());
        return userTravels.stream().map(travelJournal -> {
            CardTravelJournalDTO dto = modelMapper.map(travelJournal, CardTravelJournalDTO.class);
            int notesNumber = travelJournal.getNoteList().size();
            dto.setNotesNumber(notesNumber);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public TravelJournal getTravelJournalById(int id) {
        return travelRepository.findById(id).orElse(null);
    }

    @Override
    public TravelJournal findByUserUserIdAndTravelId(int userId, int travelId){
        return travelRepository.findByUserUserIdAndTravelId(userId, travelId);
    }

}


