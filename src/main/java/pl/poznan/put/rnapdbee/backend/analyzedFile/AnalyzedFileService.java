package pl.poznan.put.rnapdbee.backend.analyzedFile;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbClient;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbFileDataEntity;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.AnalyzedFileEntityNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.InvalidPdbIdException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileUnzipException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.repository.AnalyzedFileRepository;
import pl.poznan.put.rnapdbee.backend.analyzedFile.repository.PdbFileDataRepository;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
    private final PdbFileDataRepository pdbFileDataRepository;
    private final PdbClient pdbClient;
    private final MessageProvider messageProvider;
    private final Logger logger = LoggerFactory.getLogger(AnalyzedFileService.class);
    @Value("${document.storage.days}")
    private int documentStorageDays;

    @Autowired
    private AnalyzedFileService(
            AnalyzedFileRepository analyzedFileRepository,
            PdbFileDataRepository pdbFileDataRepository,
            PdbClient pdbClient,
            MessageProvider messageProvider
    ) {
        this.analyzedFileRepository = analyzedFileRepository;
        this.pdbFileDataRepository = pdbFileDataRepository;
        this.pdbClient = pdbClient;
        this.messageProvider = messageProvider;
    }

    public String findAnalyzedFile(UUID id) {
        String analyzedFile = analyzedFileRepository.findById(id.toString());
        if (analyzedFile == null) {
            logger.error(String.format("File with id: [%s] to reanalyze not found in analyzedFileRepository.", id));

            throw new AnalyzedFileEntityNotFoundException(
                    messageProvider.getMessage(MessageProvider.Message.FILE_NOT_FOUND));
        }

        return analyzedFile;
    }

    public String findPdbFile(String id) {
        String pdbFile = analyzedFileRepository.findById(id);
        if (pdbFile == null) {
            logger.error(String.format("File '%s.cif' from Protein Data Bank not found in analyzedFileRepository.", id));

            throw new PdbFileNotFoundException(
                    messageProvider.getMessage(MessageProvider.Message.PDB_FILE_NOT_FOUND_FORMAT), id);
        }

        return pdbFile;
    }

    public void saveAnalyzedFile(
            UUID id,
            String filename,
            String content) {
        analyzedFileRepository.save(id.toString(), filename, content);
    }

    public void deleteAnalyzedFile(UUID id) {
        analyzedFileRepository.delete(id.toString());
    }

    public void deleteExpiredPdbFiles() {
        List<PdbFileDataEntity> pdbFileDataList = pdbFileDataRepository.findAll(Sort.by("createdAt"));
        List<String> expiredPdbFilesIds = new ArrayList<>();

        for (PdbFileDataEntity pdbFileData : pdbFileDataList) {
            if ((int) Duration.between(pdbFileData.getCreatedAt(), Instant.now()).toDays() >= documentStorageDays) {
                String expiredFileId = pdbFileData.getId();
                expiredPdbFilesIds.add(expiredFileId);
                analyzedFileRepository.delete(expiredFileId);
            } else {
                break;
            }
        }

        pdbFileDataRepository.deleteAllById(expiredPdbFilesIds);
    }

    public String fetchPdbStructure(String pdbId) {
        validatePdbId(pdbId);
        String pdbFile = analyzedFileRepository.findById(pdbId);

        if (pdbFile == null) {
            String fileContent = downloadPdbFile(pdbId);
            analyzedFileRepository.save(pdbId, pdbId + PDB_FILE_EXTENSION, fileContent);

            PdbFileDataEntity pdbFileDataEntity = new PdbFileDataEntity.Builder()
                    .withId(pdbId)
                    .withCreatedAt(Instant.now())
                    .build();
            pdbFileDataRepository.save(pdbFileDataEntity);

            return fileContent;
        } else {
            Optional<PdbFileDataEntity> optionalPdbFileData = pdbFileDataRepository.findById(pdbId);

            if (optionalPdbFileData.isEmpty()) {
                logger.warn("File Data '%s.cif' from Protein Data Bank not found in pdbFileDataRepository.");
                return pdbFile;
            }

            PdbFileDataEntity pdbFileDataEntity = optionalPdbFileData.get();
            pdbFileDataEntity.setCreatedAt(Instant.now());
            pdbFileDataRepository.save(pdbFileDataEntity);

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
                    messageProvider.getMessage(MessageProvider.Message.PDB_FILE_UNZIP_FORMAT), pdbId);
        }
    }

    private String downloadPdbFile(String pdbId) {
        logger.info(String.format("Downloading file with pdbId: [%s] form Protein Data Bank", pdbId));
        String fileExtension = PDB_FILE_EXTENSION + ARCHIVE_FILE_EXTENSION;
        byte[] pdbResponse = pdbClient.performPdbRequest(pdbId, fileExtension);
        return unzipPdbFile(pdbResponse, pdbId);
    }

    private void validatePdbId(String pdbId) {
        if (pdbId.length() != 4) {
            logger.error(String.format("Invalid PDB id: '%s', 4 characters required in id.", pdbId));

            throw new InvalidPdbIdException(
                    messageProvider.getMessage(MessageProvider.Message.INVALID_PDB_ID_FORMAT), pdbId);
        }
    }
}
