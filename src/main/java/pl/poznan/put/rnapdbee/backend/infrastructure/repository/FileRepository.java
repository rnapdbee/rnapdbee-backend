package pl.poznan.put.rnapdbee.backend.infrastructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.rnapdbee.backend.shared.domain.FileEntity;

import java.util.UUID;

public interface FileRepository extends MongoRepository<FileEntity, UUID> {
}
