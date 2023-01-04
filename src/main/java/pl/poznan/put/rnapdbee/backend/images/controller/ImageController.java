package pl.poznan.put.rnapdbee.backend.images.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController("/image")
public class ImageController {

    private final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Value("${svg.images.directory.path}")
    private String imagesPath;

    @GetMapping("/{name}")
    public ResponseEntity<Resource> getSvgImage(@PathVariable String name) {
        logger.info(String.format("GET request for svg image with name: [%s]", name));
        String inputFile = String.format("%s/%s", imagesPath, name);
        Path path = new File(inputFile).toPath();
        FileSystemResource resource = new FileSystemResource(path);
        try {
            MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(path));
            return ResponseEntity
                    .ok()
                    .contentType(mediaType)
                    .body(resource);
        } catch (IOException e) {
            logger.error(String.format("Error occurred during fetching [%s] svg image.", name), e);
            throw new RuntimeException(e);
        }
    }
}
