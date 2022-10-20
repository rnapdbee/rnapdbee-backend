package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryMongoEntity;

import java.util.UUID;

public interface TertiaryToMultiSecondaryRepository extends MongoRepository<TertiaryToMultiSecondaryMongoEntity, UUID> {
}
