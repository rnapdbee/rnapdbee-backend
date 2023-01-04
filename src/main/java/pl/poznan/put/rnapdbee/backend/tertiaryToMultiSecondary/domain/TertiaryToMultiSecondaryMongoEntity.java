package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.MongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class representing tertiary to multi secondary scenario analysis data
 */
@Document
public class TertiaryToMultiSecondaryMongoEntity extends MongoEntity<TertiaryToMultiSecondaryParams, OutputMulti<ImageInformationPath, ConsensualVisualizationPath>> {

    private TertiaryToMultiSecondaryMongoEntity(
            UUID id,
            String filename,
            List<ResultEntity<TertiaryToMultiSecondaryParams, OutputMulti<ImageInformationPath, ConsensualVisualizationPath>>> results,
            Instant createdAt,
            Boolean usePdb
    ) {
        super(id, filename, results, createdAt, usePdb);
    }

    public static TertiaryToMultiSecondaryMongoEntity of(
            UUID id,
            String filename,
            ResultEntity<TertiaryToMultiSecondaryParams, OutputMulti<ImageInformationPath, ConsensualVisualizationPath>> resultEntity,
            Boolean usePdb
    ) {
        return new TertiaryToMultiSecondaryMongoEntity.Builder()
                .withId(id)
                .withFilename(filename)
                .withResults(new ArrayList<>(List.of(resultEntity)))
                .withCreatedAt(Instant.now())
                .withUsePdb(usePdb)
                .build();
    }

    public static class Builder extends MongoEntity.Builder<Builder, TertiaryToMultiSecondaryParams, OutputMulti<ImageInformationPath, ConsensualVisualizationPath>> {

        @Override
        public TertiaryToMultiSecondaryMongoEntity build() {
            return new TertiaryToMultiSecondaryMongoEntity(
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
