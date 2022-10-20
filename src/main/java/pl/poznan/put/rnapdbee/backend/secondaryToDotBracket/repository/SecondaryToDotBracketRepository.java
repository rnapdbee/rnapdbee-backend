package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;

import java.util.UUID;

public interface SecondaryToDotBracketRepository extends MongoRepository<SecondaryToDotBracketMongoEntity, UUID> {
}
