package pl.poznan.put.rnapdbee.backend.shared;

import org.springframework.beans.factory.annotation.Value;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.shared.exception.DocumentExpiredException;
import pl.poznan.put.rnapdbee.backend.shared.exception.FilenameNotSetException;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseAnalyzeService {

    protected final AnalyzedFileService analyzedFileService;
    @Value("${document.storage.days}")
    private int documentStorageDays;

    protected BaseAnalyzeService(AnalyzedFileService analyzedFileService) {
        this.analyzedFileService = analyzedFileService;
    }

    protected String removeFileExtension(
            String filename,
            boolean removeAllExtensions) {
        String extensionsPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");

        return Optional.ofNullable(filename)
                .filter(String::isEmpty)
                .map(f -> f.replaceAll(extensionsPattern, ""))
                .orElseThrow(FilenameNotSetException::new);
    }

    protected void checkDocumentExpiration(
            Instant createdAt,
            UUID id) {
        if ((int) Duration.between(createdAt, Instant.now()).toDays() >= documentStorageDays)
            throw new DocumentExpiredException(id);
    }

    protected String getFileContentToReanalyze(
            UUID id,
            String filename,
            boolean usePdb
    ) {
        if (usePdb) {
            return analyzedFileService.findPdbFile(
                    removeFileExtension(filename, true)).getContent();
        } else {
            return analyzedFileService.findAnalyzedFile(id).getContent();
        }
    }
}
