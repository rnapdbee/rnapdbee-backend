package pl.poznan.put.rnapdbee.backend.shared;

import org.springframework.http.ContentDisposition;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.shared.exception.DocumentExpiredException;
import pl.poznan.put.rnapdbee.backend.shared.exception.FilenameNotSetException;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public abstract class BaseAnalyzeService {

    protected final AnalyzedFileService analyzedFileService;

    protected BaseAnalyzeService(AnalyzedFileService analyzedFileService) {
        this.analyzedFileService = analyzedFileService;
    }

    protected String validateContentDisposition(String contentDisposition) {
        if (contentDisposition == null || contentDisposition.isEmpty()) {
            throw new FilenameNotSetException(FilenameNotSetException.CONTENT_DISPOSITION_NOT_SET);
        }

        try {
            String filename = ContentDisposition.parse(contentDisposition).getFilename();
            if (filename == null || filename.isEmpty())
                throw new FilenameNotSetException();
            return filename;

        } catch (IllegalArgumentException exception) {
            throw new FilenameNotSetException(FilenameNotSetException.FILENAME_NOT_PARSABLE);
        }
    }

    protected String contentDispositionHeaderBuilder(String filename) {
        return ContentDisposition.builder("attachment")
                .filename(filename)
                .build()
                .toString();
    }

    protected String removeFileExtension(
            String filename,
            boolean removeAllExtensions) {
        if (filename == null || filename.isEmpty()) {
            throw new FilenameNotSetException();
        }

        String extensionsPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");
        return filename.replaceAll(extensionsPattern, "");
    }

    protected void checkDocumentExpiration(
            Instant createdAt,
            UUID id) {
        if ((int) Duration.between(createdAt, Instant.now()).toDays() >= 14)
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
