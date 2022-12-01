package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.MongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document
public class TertiaryToMultiSecondaryMongoEntity extends MongoEntity<TertiaryToMultiSecondaryParams, Output2D> {

    private TertiaryToMultiSecondaryMongoEntity(
            UUID id,
            String filename,
            List<ResultEntity<TertiaryToMultiSecondaryParams, Output2D>> results,
            Instant createdAt) {
        super(id, filename, results, createdAt);
    }

    public static class Builder extends MongoEntity.Builder<Builder, TertiaryToMultiSecondaryParams, Output2D> {

        @Override
        public TertiaryToMultiSecondaryMongoEntity build() {
            return new TertiaryToMultiSecondaryMongoEntity(
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
