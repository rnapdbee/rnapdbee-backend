package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.MongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document
public class TertiaryToDotBracketMongoEntity extends MongoEntity<TertiaryToDotBracketParams, Output3D<ImageInformationPath>> {

    private TertiaryToDotBracketMongoEntity(
            UUID id,
            String filename,
            List<ResultEntity<TertiaryToDotBracketParams, Output3D<ImageInformationPath>>> results,
            Instant createdAt,
            boolean usePdb
    ) {
        super(id, filename, results, createdAt, usePdb);
    }

    public static TertiaryToDotBracketMongoEntity of(
            UUID id,
            String filename,
            ResultEntity<TertiaryToDotBracketParams, Output3D<ImageInformationPath>> resultEntity,
            boolean usePdb
    ) {
        return new TertiaryToDotBracketMongoEntity.Builder()
                .withId(id)
                .withFilename(filename)
                .withResults(new ArrayList<>(List.of(resultEntity)))
                .withCreatedAt(Instant.now())
                .withUsePdb(usePdb)
                .build();
    }

    public static class Builder extends MongoEntity.Builder<Builder, TertiaryToDotBracketParams, Output3D<ImageInformationPath>> {

        @Override
        public TertiaryToDotBracketMongoEntity build() {
            return new TertiaryToDotBracketMongoEntity(
                    this.getId(),
                    this.getFilename(),
                    this.getResults(),
                    this.getCreatedAt(),
                    this.isUsePdb());
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
