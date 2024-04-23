package travel.journal.api.models;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FileModel {
    private Integer fileId;
    private String fileName;
    private String title;
    private byte[] fileContent;
    private LocalDate createdDate;

}
