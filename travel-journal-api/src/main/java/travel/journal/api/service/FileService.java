package travel.journal.api.service;

import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.entities.File;

import java.io.IOException;

public interface FileService {
    File saveImage(MultipartFile file) throws IOException;

    void deleteImage(int id);

    File modifyImage(int id, MultipartFile file) throws IOException;

    File CheckAndSaveImage(MultipartFile file) throws IOException;

    File getImageById(int id);
}
