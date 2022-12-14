package pl.poznan.put.rnapdbee.backend.analyzedFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntity;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntityNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.InvalidPdbIdException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbClient;
import pl.poznan.put.rnapdbee.backend.analyzedFile.repository.AnalyzedFileRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class AnalyzedFileService {

    private final AnalyzedFileRepository analyzedFileRepository;
    private final PdbClient pdbClient;

    @Autowired
    private AnalyzedFileService(
            AnalyzedFileRepository analyzedFileRepository,
            PdbClient pdbClient
    ) {
        this.analyzedFileRepository = analyzedFileRepository;
        this.pdbClient = pdbClient;
    }

    public AnalyzedFileEntity findAnalyzedFile(UUID id) {
        Optional<AnalyzedFileEntity> analyzedFile = analyzedFileRepository.findById(id);
        if (analyzedFile.isEmpty())
            throw new AnalyzedFileEntityNotFoundException();

        return analyzedFile.get();
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

    public String downloadPdbFile(String pdbId) {

        if (pdbId.length() != 4) {
            throw new InvalidPdbIdException(pdbId);
        }

        return pdbClient.performPdbRequest(pdbId);
    }
}
