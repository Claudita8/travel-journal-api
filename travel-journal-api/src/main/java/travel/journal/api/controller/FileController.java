package travel.journal.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.journal.api.entities.Files;
import travel.journal.api.service.FileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel-journal/")
public class FileController {

    private final FileService fileService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("image/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable int imageId) {
        Files image = fileService.getImageById(imageId);
        if (image == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("fileName", image.getFileName());
        return new ResponseEntity<>(image.getFileContent(), headers, HttpStatus.OK);
    }

}
