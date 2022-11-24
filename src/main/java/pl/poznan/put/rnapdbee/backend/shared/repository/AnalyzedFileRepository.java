package pl.poznan.put.rnapdbee.backend.shared.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.AnalyzedFileEntity;

import java.util.UUID;

public interface AnalyzedFileRepository extends MongoRepository<AnalyzedFileEntity, UUID> {
}
