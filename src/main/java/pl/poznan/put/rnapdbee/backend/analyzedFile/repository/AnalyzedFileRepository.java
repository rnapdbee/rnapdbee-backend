package pl.poznan.put.rnapdbee.backend.analyzedFile.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntity;

import java.util.UUID;

public interface AnalyzedFileRepository extends MongoRepository<AnalyzedFileEntity, UUID> {
}
