package pl.poznan.put.rnapdbee.backend.shared.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Id;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class MongoEntity<T, O> {
    @Id
    protected final UUID id;
    protected String fileName;
    protected List<ResultEntity<T, O>> results;

    @JsonIgnore
    protected Instant createdAt;

    protected MongoEntity(
            UUID id,
            String fileName,
            List<ResultEntity<T, O>> results,
            Instant createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.results = results;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public List<ResultEntity<T, O>> getResults() {
        return results;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }


    protected abstract static class Builder<B extends Builder<B, T, O>, T, O> {
        private UUID id;
        private String fileName = "";
        private List<ResultEntity<T, O>> results = new ArrayList<>();
        private Instant createdAt = Instant.now();

        protected abstract B self();

        public B withId(UUID id) {
            this.id = id;
            return self();
        }

        public B withFileName(String fileName) {
            if (fileName.isBlank()) {
                throw new IllegalArgumentException();
            }

            this.fileName = fileName;
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

        protected abstract MongoEntity<T, O> build();

        public UUID getId() {
            return id;
        }

        public String getFileName() {
            return fileName;
        }

        public List<ResultEntity<T, O>> getResults() {
            return results;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }
    }
}
