package pl.poznan.put.rnapdbee.backend.analyzedFile.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbFileDataEntity;

public interface PdbFileDataRepository extends MongoRepository<PdbFileDataEntity, String> {
}
