package pl.poznan.put.rnapdbee.backend.shared.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.UUID;

@Document
public class FileEntity {
    @Id
    private UUID id;
    private String content;
}
