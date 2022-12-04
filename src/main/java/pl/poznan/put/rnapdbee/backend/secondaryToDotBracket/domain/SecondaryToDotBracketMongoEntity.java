package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.MongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document
public class SecondaryToDotBracketMongoEntity extends MongoEntity<SecondaryToDotBracketParams, Output2D<ImageInformationPath>> {

    private SecondaryToDotBracketMongoEntity(
            UUID id,
            String filename,
            List<ResultEntity<SecondaryToDotBracketParams, Output2D<ImageInformationPath>>> results,
            Instant createdAt) {
        super(id, filename, results, createdAt);
    }

    public static SecondaryToDotBracketMongoEntity of(
            UUID id,
            String filename,
            ResultEntity<SecondaryToDotBracketParams, Output2D<ImageInformationPath>> resultEntity
    ) {
        return new SecondaryToDotBracketMongoEntity.Builder()
                .withId(id)
                .withFilename(filename)
                .withResults(new ArrayList<>(List.of(resultEntity)))
                .withCreatedAt(Instant.now())
                .build();
    }

    public static class Builder extends MongoEntity.Builder<Builder, SecondaryToDotBracketParams, Output2D<ImageInformationPath>> {

        @Override
        public SecondaryToDotBracketMongoEntity build() {
            return new SecondaryToDotBracketMongoEntity(
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
