package pl.poznan.put.rnapdbee.backend.analyzedFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntity;
import pl.poznan.put.rnapdbee.backend.analyzedFile.repository.AnalyzedFileRepository;
import pl.poznan.put.rnapdbee.backend.shared.exception.domain.AnalyzedFileEntityNotExistException;

import java.util.Optional;
import java.util.UUID;

@Service
public class AnalyzedFileService {

    private final AnalyzedFileRepository analyzedFileRepository;

    @Autowired
    private AnalyzedFileService(
            AnalyzedFileRepository analyzedFileRepository
    ) {
        this.analyzedFileRepository = analyzedFileRepository;
    }

    public AnalyzedFileEntity findAnalyzedFile(UUID id) {
        Optional<AnalyzedFileEntity> analyzedFile = analyzedFileRepository.findById(id);
        if (analyzedFile.isEmpty())
            throw new AnalyzedFileEntityNotExistException(
                    "file to reanalyze not found");

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
}
