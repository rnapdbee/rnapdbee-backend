package pl.poznan.put.rnapdbee.backend.analyzedFile.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.Instant;

/**
 * Class representing data of analyzed file downloaded from Protein Data Bank.
 */
@Document
public class PdbFileDataEntity {
    /**
     * Filename without extension.
     */
    @Id
    private final String id;
    private Instant createdAt;

    private PdbFileDataEntity(
            String id,
            Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private String id;
        private Instant createdAt;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PdbFileDataEntity build() {
            return new PdbFileDataEntity(
                    this.id,
                    this.createdAt);
        }
    }
}
