package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import pl.poznan.put.rnapdbee.backend.shared.domain.MongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.ResultEntity;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Document
public class TertiaryToMultiSecondaryMongoEntity extends MongoEntity<TertiaryToMultiSecondaryParamsEntity> {
    public TertiaryToMultiSecondaryMongoEntity(
            UUID id,
            String filename,
            Set<ResultEntity<TertiaryToMultiSecondaryParamsEntity>> results,
            Instant createAt) {
        super(id, filename, results, createAt);
    }
}
