package travel.journal.api.dto.travelJournal.outbound;

import lombok.Data;
import travel.journal.api.entities.File;

import java.time.LocalDate;
import java.util.List;

@Data
public class TravelJournalDetailsDTO {
    private Integer userId;
    private Integer travelId;
    private File coverPhoto;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double budget;
    private String description;
    private Integer notesNumber;
    private List<NoteEntryDTO> notesList;
}
