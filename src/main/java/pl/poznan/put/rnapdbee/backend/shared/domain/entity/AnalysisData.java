package pl.poznan.put.rnapdbee.backend.shared.domain.entity;

import javax.persistence.Id;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AnalysisData {
    @Id
    private final UUID id;
    private final String filename;
    private final Instant createdAt;
    private final Boolean usePdb;
    private final List<UUID> results;

    private AnalysisData(
            UUID id,
            String filename,
            List<UUID> results,
            Instant createdAt,
            Boolean usePdb) {
        this.id = id;
        this.filename = filename;
        this.results = results;
        this.createdAt = createdAt;
        this.usePdb = usePdb;
    }

    public void addResult(
            UUID newResultId) {
        this.results.add(0, newResultId);
    }

    public Boolean getUsePdb() {
        return usePdb;
    }

    public List<UUID> getResults() {
        return results;
    }

    public UUID getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public static class Builder {
        private UUID id;
        private String filename;
        private List<UUID> results;
        private Instant createdAt = Instant.now();
        private Boolean usePdb;

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder withResults(List<UUID> results) {
            this.results = results;
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withUsePdb(Boolean usePdb) {
            this.usePdb = usePdb;
            return this;
        }

        public AnalysisData build() {
            return new AnalysisData(
                    this.id,
                    this.filename,
                    this.results,
                    this.createdAt,
                    this.usePdb);
        }
    }
}
