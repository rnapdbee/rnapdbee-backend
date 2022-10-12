package pl.poznan.put.rnapdbee.backend.shared.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface FileRepository extends MongoRepository<FileRepository, UUID> {
}
