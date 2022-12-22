package pl.poznan.put.rnapdbee.backend.analyzedFile.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.InvalidPdbIdException;

import javax.persistence.Id;
import java.time.Instant;


@Document
public class PdbFileEntity {
    @Id
    private final String id;
    private final String content;

    private Instant createdAt;

    private PdbFileEntity(
            String id,
            String content,
            Instant createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private String id;
        private String content;
        private Instant createdAt;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PdbFileEntity build() {
            return new PdbFileEntity(
                    this.id,
                    this.content,
                    this.createdAt);
        }
    }
}
