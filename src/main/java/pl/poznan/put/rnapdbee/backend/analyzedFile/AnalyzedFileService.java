package pl.poznan.put.rnapdbee.backend.analyzedFile;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntity;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbClient;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbFileEntity;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.AnalyzedFileEntityNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.InvalidPdbIdException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileUnzipException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.repository.AnalyzedFileRepository;
import pl.poznan.put.rnapdbee.backend.analyzedFile.repository.PdbFileRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

@Service
public class AnalyzedFileService {

    public static final String PDB_FILE_EXTENSION = ".cif";
    private static final String ARCHIVE_FILE_EXTENSION = ".gz";

    private final AnalyzedFileRepository analyzedFileRepository;
    private final PdbFileRepository pdbFileRepository;
    private final PdbClient pdbClient;

    @Autowired
    private AnalyzedFileService(
            AnalyzedFileRepository analyzedFileRepository,
            PdbFileRepository pdbFileRepository,
            PdbClient pdbClient
    ) {
        this.analyzedFileRepository = analyzedFileRepository;
        this.pdbFileRepository = pdbFileRepository;
        this.pdbClient = pdbClient;
    }

    public AnalyzedFileEntity findAnalyzedFile(UUID id) {
        Optional<AnalyzedFileEntity> analyzedFile = analyzedFileRepository.findById(id);
        if (analyzedFile.isEmpty())
            throw new AnalyzedFileEntityNotFoundException();

        return analyzedFile.get();
    }

    public PdbFileEntity findPdbFile(String id) {
        Optional<PdbFileEntity> pdbFile = pdbFileRepository.findById(id);
        if (pdbFile.isEmpty())
            throw new PdbFileNotFoundException(id);

        return pdbFile.get();
    }

    public void saveAnalyzedFile(
            UUID id,
            String content
    ) {
        analyzedFileRepository.save(new AnalyzedFileEntity.Builder()
                .withId(id)
                .withContent(content)
                .build());
    }

    public PdbFileEntity fetchPdbStructure(String pdbId) {
        validatePdbId(pdbId);
        Optional<PdbFileEntity> optionalPdbFile = pdbFileRepository.findById(pdbId);

        if (optionalPdbFile.isEmpty()) {
            String fileContent = downloadPdbFile(pdbId);

            PdbFileEntity pdbFile = new PdbFileEntity.Builder()
                    .withId(pdbId)
                    .withContent(fileContent)
                    .withCreatedAt(Instant.now())
                    .build();
            pdbFileRepository.save(pdbFile);

            return pdbFile;

        } else {
            PdbFileEntity pdbFile = optionalPdbFile.get();
            pdbFile.setCreatedAt(Instant.now());
            pdbFileRepository.save(pdbFile);

            return pdbFile;
        }
    }

    private String unzipPdbFile(
            byte[] byteArray,
            String pdbId) {
        try {
            InputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(byteArray));

            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            throw new PdbFileUnzipException(pdbId);
        }
    }

    private String downloadPdbFile(String pdbId) {
        String fileExtension = PDB_FILE_EXTENSION + ARCHIVE_FILE_EXTENSION;
        byte[] pdbResponse = pdbClient.performPdbRequest(pdbId, fileExtension);
        return unzipPdbFile(pdbResponse, pdbId);
    }

    private void validatePdbId(String pdbId) {
        if (pdbId.length() != 4) {
            throw new InvalidPdbIdException(pdbId);
        }
    }
}
