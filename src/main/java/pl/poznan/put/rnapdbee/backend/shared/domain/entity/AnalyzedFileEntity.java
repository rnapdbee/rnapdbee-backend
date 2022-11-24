package pl.poznan.put.rnapdbee.backend.shared.domain.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.UUID;

@Document
public class AnalyzedFileEntity {
    @Id
    private UUID id;
    private String content;
    private String contentDisposition;

    private AnalyzedFileEntity(
            UUID id,
            String content,
            String contentDisposition) {
        this.id = id;
        this.content = content;
        this.contentDisposition = contentDisposition;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }

    public static class Builder {
        private UUID id;
        private String content;
        private String contentDisposition;

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withContentDisposition(String contentDisposition) {
            this.contentDisposition = contentDisposition;
            return this;
        }

        public AnalyzedFileEntity build() {
            return new AnalyzedFileEntity(
                    this.id,
                    this.content,
                    this.contentDisposition);
        }
    }
}
