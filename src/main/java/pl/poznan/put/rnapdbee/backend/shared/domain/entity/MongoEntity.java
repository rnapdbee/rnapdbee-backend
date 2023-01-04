package pl.poznan.put.rnapdbee.backend.shared.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Id;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Mongo document design for storing analysis data.
 *
 * @param <T> analysis parameters
 * @param <O> analysis results, output from engine service
 */
public abstract class MongoEntity<T, O> {
    @Id
    protected final UUID id;
    protected final String filename;
    protected List<ResultEntity<T, O>> results;
    @JsonIgnore
    protected final Instant createdAt;
    @JsonIgnore
    protected final Boolean usePdb;

    protected MongoEntity(
            UUID id,
            String filename,
            List<ResultEntity<T, O>> results,
            Instant createdAt,
            Boolean usePdb) {
        this.id = id;
        this.filename = filename;
        this.results = results;
        this.createdAt = createdAt;
        this.usePdb = usePdb;
    }

    public void addResult(
            ResultEntity<T, O> newResult) {
        this.results.add(0, newResult);
    }

    public UUID getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public List<ResultEntity<T, O>> getResults() {
        return results;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Boolean isUsePdb() {
        return usePdb;
    }

    protected abstract static class Builder<B extends Builder<B, T, O>, T, O> {
        private UUID id;
        private String filename;
        private List<ResultEntity<T, O>> results = new ArrayList<>();
        private Instant createdAt = Instant.now();

        private Boolean usePdb;

        protected abstract B self();

        public B withId(UUID id) {
            this.id = id;
            return self();
        }

        public B withFilename(String filename) {
            this.filename = filename;
            return self();
        }

        public B withResults(List<ResultEntity<T, O>> results) {
            this.results = results;
            return self();
        }

        public B withCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return self();
        }

        public B withUsePdb(Boolean usePdb) {
            this.usePdb = usePdb;
            return self();
        }

        public void addResult(
                ResultEntity<T, O> newResult) {
            this.results.add(newResult);
        }

        protected abstract MongoEntity<T, O> build();

        public UUID getId() {
            return id;
        }

        public String getFilename() {
            return filename;
        }

        public List<ResultEntity<T, O>> getResults() {
            return results;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public Boolean isUsePdb() {
            return usePdb;
        }
    }
}
