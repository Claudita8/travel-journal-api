package travel.journal.api.service;

import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.entities.Files;

import java.io.IOException;

public interface FileService {
    Files saveImage(MultipartFile file) throws IOException;

    void deleteImage(int id);

    Files modifyImage(int id, MultipartFile file) throws IOException;

    Files CheckAndSaveImage(MultipartFile file) throws IOException;

    Files getImageById(int id);
}
