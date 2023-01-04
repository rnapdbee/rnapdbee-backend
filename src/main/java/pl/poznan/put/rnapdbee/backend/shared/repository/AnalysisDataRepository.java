package pl.poznan.put.rnapdbee.backend.shared.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.AnalysisData;

import java.util.UUID;

public interface AnalysisDataRepository extends MongoRepository<AnalysisData, UUID> {
}
