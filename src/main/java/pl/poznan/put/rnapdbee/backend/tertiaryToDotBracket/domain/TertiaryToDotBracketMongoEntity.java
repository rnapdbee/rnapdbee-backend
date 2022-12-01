package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.MongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document
public class TertiaryToDotBracketMongoEntity extends MongoEntity<TertiaryToDotBracketParams, Output2D> {

    private TertiaryToDotBracketMongoEntity(
            UUID id,
            String filename,
            List<ResultEntity<TertiaryToDotBracketParams, Output2D>> results,
            Instant createdAt
    ) {
        super(id, filename, results, createdAt);
    }

    public static class Builder extends MongoEntity.Builder<Builder, TertiaryToDotBracketParams, Output2D> {

        @Override
        public TertiaryToDotBracketMongoEntity build() {
            return new TertiaryToDotBracketMongoEntity(
                    this.getId(),
                    this.getFilename(),
                    this.getResults(),
                    this.getCreatedAt());
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
