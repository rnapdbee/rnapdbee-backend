package pl.poznan.put.rnapdbee.backend.shared.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ResultRepository {
    private static final Logger logger = LoggerFactory.getLogger(ResultRepository.class);
    private final GridFsTemplate gridFsTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ResultRepository(GridFsTemplate gridFsTemplate, ObjectMapper objectMapper) {
        this.gridFsTemplate = gridFsTemplate;
        this.objectMapper = objectMapper;
    }

    public <T, O> void save(ResultEntity<T, O> resultEntity) {
        try {
            String content = objectMapper.writeValueAsString(resultEntity);
            InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

            GridFsUpload<String> upload = GridFsUpload.fromStream(stream)
                    .id(resultEntity.getId().toString())
                    .filename(resultEntity.getId().toString())
                    .build();

            gridFsTemplate.store(upload);
        } catch (IOException e) {
            logger.error("Error occurred during serializing ResultEntity to String.", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T, O> Optional<ResultEntity<T, O>> findById(UUID id) {
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id.toString())));
        if (file == null)
            return Optional.empty();

        GridFsResource resource = gridFsTemplate.getResource(file);

        try {
            String json = new String(resource.getContent().readAllBytes(), StandardCharsets.UTF_8);
            ResultEntity<T, O> resultEntity = (ResultEntity<T, O>) objectMapper.readValue(json, ResultEntity.class);
            return Optional.of(resultEntity);
        } catch (IOException e) {
            logger.error("Error occurred during converting InputStream to String or deserializing.", e);
            throw new RuntimeException(e);
        }
    }

    public void deleteById(UUID id) {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id.toString())));
    }

    public void deleteAllById(Iterable<UUID> ids) {
        ids.forEach(this::deleteById);
    }
}
