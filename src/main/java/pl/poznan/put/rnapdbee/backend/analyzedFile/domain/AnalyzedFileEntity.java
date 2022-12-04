package pl.poznan.put.rnapdbee.backend.analyzedFile.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.UUID;

@Document
public class AnalyzedFileEntity {
    @Id
    private final UUID id;
    private final String content;

    private AnalyzedFileEntity(
            UUID id,
            String content) {
        this.id = id;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public static class Builder {
        private UUID id;
        private String content;

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public AnalyzedFileEntity build() {
            return new AnalyzedFileEntity(
                    this.id,
                    this.content);
        }
    }
}
