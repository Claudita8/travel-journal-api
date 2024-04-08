package travel.journal.api.dto.travelJournal.outbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.entities.Files;
import travel.journal.api.models.FilesModel;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDetailsDTO {
    private Integer travelId;
    private Integer noteId;
    private List<Files> travelPhotosFiles;
    private String title;
    private LocalDate startDate;
    private String description;
}
