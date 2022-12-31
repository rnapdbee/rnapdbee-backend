package pl.poznan.put.rnapdbee.backend.analyzedFile;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

/**
 * Service class responsible for managing analysis files uploaded by the user and downloaded from Protein Data Bank
 */
@Service
public class AnalyzedFileService {

    public static final String PDB_FILE_EXTENSION = ".cif";
    private static final String ARCHIVE_FILE_EXTENSION = ".gz";

    private final AnalyzedFileRepository analyzedFileRepository;
    private final PdbFileRepository pdbFileRepository;
    private final PdbClient pdbClient;
    private final MessageProvider messageProvider;
    private final Logger logger = LoggerFactory.getLogger(AnalyzedFileService.class);

    @Autowired
    private AnalyzedFileService(
            AnalyzedFileRepository analyzedFileRepository,
            PdbFileRepository pdbFileRepository,
            PdbClient pdbClient,
            MessageProvider messageProvider
    ) {
        this.analyzedFileRepository = analyzedFileRepository;
        this.pdbFileRepository = pdbFileRepository;
        this.pdbClient = pdbClient;
        this.messageProvider = messageProvider;
    }

    public AnalyzedFileEntity findAnalyzedFile(UUID id) {
        Optional<AnalyzedFileEntity> analyzedFile = analyzedFileRepository.findById(id);
        if (analyzedFile.isEmpty()) {
            logger.error(String.format("File with id: [%s] to reanalyze not found in analyzedFileRepository.", id));

            throw new AnalyzedFileEntityNotFoundException(
                    messageProvider.getMessage("api.exception.file.not.found"));
        }

        return analyzedFile.get();
    }

    public PdbFileEntity findPdbFile(String id) {
        Optional<PdbFileEntity> pdbFile = pdbFileRepository.findById(id);
        if (pdbFile.isEmpty()) {
            logger.error(String.format("File '%s.cif' from Protein Data Bank not found in pdbFileRepository.", id));

            throw new PdbFileNotFoundException(
                    messageProvider.getMessage("api.exception.pdb.file.not.found.format"), id);
        }

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
            logger.error(String.format("Pdb file '%s.cif.gz' unzip problem.", pdbId), e);

            throw new PdbFileUnzipException(
                    messageProvider.getMessage("api.exception.pdb.file.unzip.format"), pdbId);
        }
    }

    private String downloadPdbFile(String pdbId) {
        String fileExtension = PDB_FILE_EXTENSION + ARCHIVE_FILE_EXTENSION;
        byte[] pdbResponse = pdbClient.performPdbRequest(pdbId, fileExtension);
        return unzipPdbFile(pdbResponse, pdbId);
    }

    private void validatePdbId(String pdbId) {
        if (pdbId.length() != 4) {
            logger.error(String.format("Invalid PDB id: '%s', 4 characters required in id.", pdbId));

            throw new InvalidPdbIdException(
                    messageProvider.getMessage("api.exception.invalid.pdb.id.format"), pdbId);
        }
    }
}
