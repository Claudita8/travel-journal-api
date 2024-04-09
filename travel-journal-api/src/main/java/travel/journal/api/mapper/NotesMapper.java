package travel.journal.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import travel.journal.api.entities.Note;
import travel.journal.api.models.NotesModel;
@Component
public class NotesMapper {
    private final ModelMapper modelMapper;

    public NotesMapper() {
        this.modelMapper = new ModelMapper();
    }

    public NotesModel toNotesModel(Note notesEntity) {
        return modelMapper.map(notesEntity, NotesModel.class);
    }

    public Note toNotesEntity(NotesModel notesModel) {
        return modelMapper.map(notesModel, Note.class);
    }
}
