package pl.poznan.put.rnapdbee.backend.shared.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.UUID;

@Document
public class AnalysedFileEntity {
    @Id
    private UUID id;
    private String content;

    public AnalysedFileEntity(
            UUID id,
            String content) {
        this.id = id;
        this.content = content;
    }
}
