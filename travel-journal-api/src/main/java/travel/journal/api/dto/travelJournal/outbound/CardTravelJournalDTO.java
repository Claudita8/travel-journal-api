package travel.journal.api.dto.travelJournal.outbound;

import lombok.Data;
import travel.journal.api.entities.Files;

import java.time.LocalDate;

@Data
public class CardTravelJournalDTO {
    private Integer travelId;
    private Integer coverPhotoId;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double budget;
    private String description;
    private Integer notesNumber;
}
