package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Document
public class SecondaryToDotBracketMongoEntity {
    @Id
    private UUID id;
    private String filename;
    private Set<SecondaryToDotBracketResultEntity> results;
    private Instant createAt;

    public SecondaryToDotBracketMongoEntity(
            UUID id,
            String filename,
            Set<SecondaryToDotBracketResultEntity> results,
            Instant createAt) {
        this.id = id;
        this.filename = filename;
        this.results = results;
        this.createAt = createAt;
    }
}
