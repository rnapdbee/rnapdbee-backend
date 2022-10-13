package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import pl.poznan.put.rnapdbee.backend.shared.domain.MongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.ResultEntity;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Document
public class SecondaryToDotBracketMongoEntity extends MongoEntity<SecondaryToDotBracketParamsEntity> {
    public SecondaryToDotBracketMongoEntity(
            UUID id,
            String filename,
            Set<ResultEntity<SecondaryToDotBracketParamsEntity>> results,
            Instant createAt) {
        super(id, filename, results, createAt);
    }
}
