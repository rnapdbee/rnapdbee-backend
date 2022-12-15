package pl.poznan.put.rnapdbee.backend.analyzedFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntity;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntityNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.InvalidPdbIdException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbClient;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbFileEntity;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbFileNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.repository.AnalyzedFileRepository;
import pl.poznan.put.rnapdbee.backend.analyzedFile.repository.PdbFileRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnalyzedFileService {

    private static final String PDB_FILE_EXTENSION = ".cif";

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

    public static String getPdbFileExtension() {
        return PDB_FILE_EXTENSION;
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

    private String downloadPdbFile(String pdbId) {
        return pdbClient.performPdbRequest(pdbId, PDB_FILE_EXTENSION);
    }

    private void validatePdbId(String pdbId) {
        if (pdbId.length() != 4) {
            throw new InvalidPdbIdException(pdbId);
        }
    }

    public PdbFileEntity getPdbFile(String pdbId) {
        validatePdbId(pdbId);

        try {
            PdbFileEntity pdbFile = findPdbFile(pdbId);

            pdbFile.setCreatedAt(Instant.now());
            pdbFileRepository.save(pdbFile);

            return pdbFile;
        } catch (PdbFileNotFoundException e) {
            String fileContent = downloadPdbFile(pdbId);

            PdbFileEntity pdbFile = new PdbFileEntity.Builder()
                    .withId(pdbId)
                    .withContent(fileContent)
                    .withCreatedAt(Instant.now())
                    .build();
            pdbFileRepository.save(pdbFile);

            return pdbFile;
        }
    }
}
