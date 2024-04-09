package travel.journal.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "\"files\"")
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //Watch out if nullable or not
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

//    @ManyToMany
//    @JsonIgnore
//    @JoinTable(
//            name = "notes_files",
//            joinColumns = @JoinColumn(name = "file_id"),
//            inverseJoinColumns = @JoinColumn(name = "note_id")
//    )
//    private List<Note> notesList;
    @ManyToMany(mappedBy = "photos",cascade = CascadeType.ALL)
    private List<Note> notesList;

//    public Files() {
//        this.notesList = new ArrayList<>();
//    }
}

