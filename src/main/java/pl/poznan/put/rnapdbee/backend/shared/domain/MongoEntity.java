package pl.poznan.put.rnapdbee.backend.shared.domain;

import javax.persistence.Id;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public abstract class MongoEntity<T> {
    @Id
    protected UUID id;
    protected String filename;
    protected Set<ResultEntity<T>> results;
    protected Instant createAt;

    public MongoEntity(UUID id, String filename, Set<ResultEntity<T>> results, Instant createAt) {
        this.id = id;
        this.filename = filename;
        this.results = results;
        this.createAt = createAt;
    }

    public UUID getId() {
        return id;
    }
}
