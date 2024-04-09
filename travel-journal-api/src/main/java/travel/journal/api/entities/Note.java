package travel.journal.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"notes\"")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private int noteId;

    @ManyToOne
    @JoinColumn(name = "travel_id")
    private TravelJournal travelJournal;

    @Column(name = "destination_name")
    private String destinationName;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "note")
    private String note;

    @ManyToMany(mappedBy = "notesList")
    private List<Files> filesList;


}

