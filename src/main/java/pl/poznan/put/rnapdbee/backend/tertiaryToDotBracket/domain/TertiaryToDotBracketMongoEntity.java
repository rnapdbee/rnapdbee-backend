package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import pl.poznan.put.rnapdbee.backend.shared.domain.FileDataEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.MongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.ResultEntity;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Document
public class TertiaryToDotBracketMongoEntity extends MongoEntity<TertiaryToDotBracketParamsEntity> {

    public TertiaryToDotBracketMongoEntity(
            UUID id,
            FileDataEntity fileData,
            Set<ResultEntity<TertiaryToDotBracketParamsEntity>> results,
            Instant createAt) {
        super(id, fileData, results, createAt);
    }
}
