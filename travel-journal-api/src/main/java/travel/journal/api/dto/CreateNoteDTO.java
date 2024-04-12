package travel.journal.api.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class CreateNoteDTO {
    @NotBlank
    private String destinationName;

    @NotBlank
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private String date;

    @NotBlank
    private String description;

}
