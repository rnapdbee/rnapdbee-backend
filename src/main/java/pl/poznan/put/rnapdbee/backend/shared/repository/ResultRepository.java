package pl.poznan.put.rnapdbee.backend.shared.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;

import java.util.UUID;

public interface ResultRepository<T, O> extends MongoRepository<ResultEntity<T, O>, UUID> {
}
