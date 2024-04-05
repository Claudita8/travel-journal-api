package travel.journal.api.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.travelJournal.inbound.TravelJournalDTO;
import travel.journal.api.dto.travelJournal.outbound.CardTravelJournalDTO;
import travel.journal.api.dto.travelJournal.outbound.TravelJournalDetailsDTO;
import travel.journal.api.security.services.UserDetailsImpl;
import travel.journal.api.service.TravelServiceImpl;

import java.io.IOException;
import java.util.List;

@RestController
@Validated
@RequestMapping("/travel-journal")
public class TravelController {
    private final TravelServiceImpl travelServiceImpl;


    public TravelController(TravelServiceImpl travelServiceImpl) {
        this.travelServiceImpl = travelServiceImpl;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/travel")
    public ResponseEntity<TravelJournalDetailsDTO> createTravel(@Valid @RequestPart("travelJournalDTO") TravelJournalDTO travelJournalDTO, @RequestParam("file") MultipartFile file) throws IOException {
        TravelJournalDetailsDTO newTravel = travelServiceImpl.createTravelJournal(travelJournalDTO, file);

        return ResponseEntity.ok(newTravel);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/travel/{id}")
    public ResponseEntity<TravelJournalDetailsDTO> getTravel(@PathVariable("id") @Positive int travelId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Integer userId;
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        userId = userDetails.getId();
        TravelJournalDetailsDTO travelToGet = travelServiceImpl.getTravelJournal(travelId, userId);
        return ResponseEntity.ok(travelToGet);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myTravels/{userId}")
    public ResponseEntity<List<CardTravelJournalDTO>> getUserTravels(@PathVariable("userId") int userId) {
        List<CardTravelJournalDTO> userTravelJournals = travelServiceImpl.getUserTravelJournal(userId);
        return ResponseEntity.ok(userTravelJournals);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/travels")
    ResponseEntity<List<TravelJournalDetailsDTO>> getAllTravels() {
        List<TravelJournalDetailsDTO> allTravels = travelServiceImpl.getAllTravelJournals();

        return ResponseEntity.ok(allTravels);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("travel/{id}")
    ResponseEntity<TravelJournalDetailsDTO> modifyTravel(@PathVariable("id") int travelId, @Valid @RequestPart TravelJournalDTO travelJournalDTO, @RequestParam("file") MultipartFile file) throws IOException {

        TravelJournalDetailsDTO modifiedTravel = travelServiceImpl.modifyTravelJournal(travelId, travelJournalDTO, file);

        return ResponseEntity.ok(modifiedTravel);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("travel/{id}")
    ResponseEntity<Void> deleteTravel(@PathVariable("id") int travelId) {
        travelServiceImpl.deleteTravelJournal(travelId);
        return ResponseEntity.noContent().build();
    }
}
