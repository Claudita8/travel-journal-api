package travel.journal.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import travel.journal.api.entities.File;
import travel.journal.api.models.FileModel;

@Component
public class FileMapper {
    private final ModelMapper modelMapper;

    public FileMapper() {
        this.modelMapper = new ModelMapper();
    }

    public FileModel toFileModel(File fileEntity) {
        return modelMapper.map(fileEntity, FileModel.class);
    }

    public File toFileEntity(FileModel fileModel) {
        return modelMapper.map(fileModel, File.class);
    }
}
