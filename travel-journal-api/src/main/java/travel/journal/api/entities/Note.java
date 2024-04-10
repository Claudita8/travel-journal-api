package travel.journal.api.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
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


    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "notesList")
    private List<Files> filesList;

}