package travel.journal.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Entity
@Table(name = "\"notes\"")
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private int noteId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "travel_id")
    private TravelJournal travelJournal;

    @NotNull
    @Column(name = "destination_name")
    private String destinationName;

    @NotNull
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;


    @Column(name = "description")
    private String description;

//    @ManyToMany(mappedBy = "notesList")
//    private List<Files> filesList;
        @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "notes_files",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<Files> photos;

}

