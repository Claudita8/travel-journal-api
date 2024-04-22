package travel.journal.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.entities.File;
import travel.journal.api.exception.ResourceNotFoundException;
import travel.journal.api.repositories.FilesRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FilesRepository filesRepository;

    @Override
    public File saveImage(MultipartFile file) throws IOException {
        File fileToCreate = new File();

        fileToCreate.setFileName(file.getOriginalFilename());
        fileToCreate.setCreatedDate(LocalDate.now());
        fileToCreate.setFileContent(file.getBytes());
        fileToCreate.setTitle(file.getName());

        return filesRepository.save(fileToCreate);
    }

    public void deleteImage(int id) {
        if (filesRepository.existsById(id)) {
            filesRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("File with id: " + id + " does not exist");
        }
    }

    public File modifyImage(int id, MultipartFile file) throws IOException {
        Optional<File> existingImageOptional = filesRepository.findById(id);

        if (existingImageOptional.isPresent()) {

            File existingFile = existingImageOptional.get();

            existingFile.setFileName(file.getOriginalFilename());
            existingFile.setFileContent(file.getBytes());
            existingFile.setTitle(file.getName());
            existingFile.setCreatedDate(LocalDate.now());

            return filesRepository.save(existingFile);
        } else {
            return this.saveImage(file);
        }
    }

    @Override
    public File CheckAndSaveImage(MultipartFile file) throws IOException {
        File existingFile = filesRepository.findByFileName(file.getOriginalFilename());

        if (existingFile != null) {
            return existingFile;
        } else {
            return saveImage(file);
        }
    }

    public File getImageById(int id) {
        return filesRepository.findById(id).orElse(null);
    }
}
