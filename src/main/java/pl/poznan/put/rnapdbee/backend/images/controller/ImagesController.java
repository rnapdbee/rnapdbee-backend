package pl.poznan.put.rnapdbee.backend.images.controller;

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

@RestController
public class ImagesController {

    @Value("${spring.svg.images.directory.path}")
    private String imagesPath;

    @GetMapping("/images/{name}")
    public ResponseEntity<Resource> getSvgImage(@PathVariable String name) throws IOException {
        String inputFile = String.format("%s/%s", imagesPath, name);
        Path path = new File(inputFile).toPath();
        FileSystemResource resource = new FileSystemResource(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
                .body(resource);
    }
}
