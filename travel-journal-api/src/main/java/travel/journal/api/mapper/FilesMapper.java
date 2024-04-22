package travel.journal.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import travel.journal.api.entities.File;
import travel.journal.api.models.FilesModel;
@Component
public class FilesMapper {
    private final ModelMapper modelMapper;

    public FilesMapper() {
        this.modelMapper = new ModelMapper();
    }

    public FilesModel toFilesModel(File fileEntity) {
        return modelMapper.map(fileEntity, FilesModel.class);
    }

    public File toFilesEntity(FilesModel filesModel) {
        return modelMapper.map(filesModel, File.class);
    }
}
