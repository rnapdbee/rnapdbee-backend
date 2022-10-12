package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Document
public class TertiaryToMultiSecondaryMongoEntity {
    @Id
    private UUID id;
    private String filename;
    private Set<TertiaryToMultiSecondaryResultEntity> results;
    private Instant createAt;

    public TertiaryToMultiSecondaryMongoEntity(
            UUID id,
            String filename,
            Set<TertiaryToMultiSecondaryResultEntity> results,
            Instant createAt) {
        this.id = id;
        this.filename = filename;
        this.results = results;
        this.createAt = createAt;
    }
}
