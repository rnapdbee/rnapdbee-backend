package pl.poznan.put.rnapdbee.backend.analyzedFile.repository;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsUpload;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Repository
public class AnalyzedFileRepository {
    private final GridFsTemplate gridFsTemplate;
    private final Logger logger = LoggerFactory.getLogger(AnalyzedFileRepository.class);

    @Autowired
    public AnalyzedFileRepository(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public void save(
            String id,
            String filename,
            String content
    ) {
        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        GridFsUpload<String> upload = GridFsUpload.fromStream(stream)
                .id(id)
                .filename(filename)
                .build();

        gridFsTemplate.store(upload);
    }

    public String findById(String id) {
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
        if (file == null)
            return null;

        GridFsResource resource = gridFsTemplate.getResource(file);

        try {
            return new String(resource.getContent().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Error occurred during converting InputStream to String.", e);
            throw new RuntimeException(e);
        }
    }
}
