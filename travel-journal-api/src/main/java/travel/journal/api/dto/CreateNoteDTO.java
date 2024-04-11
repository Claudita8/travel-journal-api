package travel.journal.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import travel.journal.api.entities.Files;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

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
