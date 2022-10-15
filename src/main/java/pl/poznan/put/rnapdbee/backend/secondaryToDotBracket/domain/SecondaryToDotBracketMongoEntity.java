package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import pl.poznan.put.rnapdbee.backend.shared.domain.FileDataEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.MongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.ResultEntity;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Document
public class SecondaryToDotBracketMongoEntity extends MongoEntity<SecondaryToDotBracketParamsEntity> {

    public SecondaryToDotBracketMongoEntity(
            UUID id,
            FileDataEntity fileData,
            Set<ResultEntity<SecondaryToDotBracketParamsEntity>> results,
            Instant createAt) {
        super(id, fileData, results, createAt);
    }
}
