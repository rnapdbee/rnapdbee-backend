package pl.poznan.put.rnapdbee.backend.infrastructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.rnapdbee.backend.shared.domain.AnalysedFileEntity;

import java.util.UUID;

public interface AnalysedFileRepository extends MongoRepository<AnalysedFileEntity, UUID> {
}
