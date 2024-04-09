
package travel.journal.api.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.travelJournal.inbound.TravelJournalDTO;
import travel.journal.api.dto.travelJournal.outbound.TravelJournalDetailsDTO;
import travel.journal.api.entities.Files;
import travel.journal.api.entities.TravelJournal;
import travel.journal.api.entities.User;
import travel.journal.api.exception.NoPermissionException;
import travel.journal.api.exception.ResourceNotFoundException;
import travel.journal.api.exception.UnauthorizedAccesException;
import travel.journal.api.repositories.TravelJournalRepository;

import java.io.IOException;
import java.util.List;
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
            return modelMapper.map(travel, TravelJournalDetailsDTO.class);
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
            if (user.isPresent()) {
                if (checkuser.equals(existingTravelOptional.get().getUser())) {
                    TravelJournal existingTravel = existingTravelOptional.get();

                    Files modifiedImage = filesService.modifyImage(existingTravel.getCoverPhoto().getFileId(), file);

                    existingTravel.setHasCoverPhoto(modifiedImage.getFileContent().length > 0);
                    existingTravel.setCoverPhoto(modifiedImage);
                    existingTravel.setBudget(travelJournalDTO.getBudget());
                    existingTravel.setDescription(travelJournalDTO.getDescription());
                    existingTravel.setLocation(travelJournalDTO.getLocation());
                    existingTravel.setEndDate(travelJournalDTO.getEndDate());
                    existingTravel.setStartDate(travelJournalDTO.getStartDate());
                    existingTravel.setHasCoverPhoto(existingTravel.getHasCoverPhoto());
                    existingTravel.setNotesList(existingTravel.getNotesList());

                    TravelJournal modifiedTravel = travelRepository.save(existingTravel);

                    return modelMapper.map(modifiedTravel, TravelJournalDetailsDTO.class);
                } else{
                    throw new UnauthorizedAccesException("Current user is not authorized to update this travel journal");
                }
            }
        } else {
            throw new ResourceNotFoundException("Travel with id: " + id + " does not exist");
        }
        return null;
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
                    TravelJournal travelToDelete = travelOptional.get();
                    filesService.deleteImage(travelToDelete.getCoverPhoto().getFileId());
                    travelRepository.deleteById(id);

                } else {
                    throw new UnauthorizedAccesException("Current user is not authorized to delete this travel journal");
                }
            }

        }
            else{
                    throw new ResourceNotFoundException("Travel with id: " + id + " does not exist");
                }
    }


    @Override
    public List<TravelJournalDetailsDTO> getUserTravelJournal(int userId) {
        List<TravelJournal> userTravels = travelRepository.findByUserUserIdOrderByStartDateDesc(userId);
        return userTravels.stream().map(travelJournal -> modelMapper.map(travelJournal, TravelJournalDetailsDTO.class)).collect(Collectors.toList());
    }

    @Override
    public TravelJournal getTravelJourbalById(int id) {
        return travelRepository.findById(id).orElse(null);
    }
}


