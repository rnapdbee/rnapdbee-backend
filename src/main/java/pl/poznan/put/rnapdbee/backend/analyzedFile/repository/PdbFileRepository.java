package pl.poznan.put.rnapdbee.backend.analyzedFile.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbFileEntity;

public interface PdbFileRepository extends MongoRepository<PdbFileEntity, String> {
}
