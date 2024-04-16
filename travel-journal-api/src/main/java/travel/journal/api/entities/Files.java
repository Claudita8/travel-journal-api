package travel.journal.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "\"files\"")
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int fileId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "title")
    private String title;

    @Column(name = "file_content")
    private byte[] fileContent;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @OneToMany(mappedBy = "coverPhoto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TravelJournal> travelJournalList;

    @ManyToMany(mappedBy = "photos", cascade = CascadeType.ALL)
    private List<Note> notesList;
}
