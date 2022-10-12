package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketMongoEntity;

import java.util.UUID;

public interface TertiaryToDotBracketRepository extends MongoRepository<TertiaryToDotBracketMongoEntity, UUID> {
}
