package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Document
public class TertiaryToDotBracketMongoEntity {
    @Id
    private UUID id;
    private String filename;
    private Set<TertiaryToDotBracketResultEntity> results;
    private Instant createAt;

    public TertiaryToDotBracketMongoEntity(
            UUID id,
            String filename,
            Set<TertiaryToDotBracketResultEntity> results,
            Instant createAt) {
        this.id = id;
        this.filename = filename;
        this.results = results;
        this.createAt = createAt;
    }

    public UUID getId() {
        return id;
    }
}
