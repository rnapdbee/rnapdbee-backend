package pl.poznan.put.rnapdbee.backend.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.images.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.AnalysisData;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.MongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.Scenario;
import pl.poznan.put.rnapdbee.backend.shared.exception.DocumentExpiredException;
import pl.poznan.put.rnapdbee.backend.shared.exception.FilenameNotSetException;
import pl.poznan.put.rnapdbee.backend.shared.exception.IdNotFoundException;
import pl.poznan.put.rnapdbee.backend.shared.repository.AnalysisDataRepository;
import pl.poznan.put.rnapdbee.backend.shared.repository.ResultRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base analyze service class containing reusable methods.
 */
public abstract class BaseAnalyzeService<T, O, E extends MongoEntity<T, O>> {

    protected static final Logger logger = LoggerFactory.getLogger(BaseAnalyzeService.class);
    protected final EngineClient engineClient;
    protected final ImageComponent imageComponent;
    protected final AnalyzedFileService analyzedFileService;
    protected final MessageProvider messageProvider;
    protected final AnalysisDataRepository analysisDataRepository;
    protected final ResultRepository<T, O> resultRepository;
    protected final Scenario scenario;
    @Value("${document.storage.days}")
    private int documentStorageDays;

    protected BaseAnalyzeService(
            EngineClient engineClient,
            ImageComponent imageComponent,
            AnalyzedFileService analyzedFileService,
            MessageProvider messageProvider,
            AnalysisDataRepository analysisDataRepository,
            ResultRepository<T, O> resultRepository,
            Scenario scenario) {
        this.engineClient = engineClient;
        this.imageComponent = imageComponent;
        this.analyzedFileService = analyzedFileService;
        this.messageProvider = messageProvider;
        this.analysisDataRepository = analysisDataRepository;
        this.resultRepository = resultRepository;
        this.scenario = scenario;
    }

    public abstract E findDocument(UUID id);

    public abstract void deleteExpiredResults(List<UUID> expiredResultsIds);

    protected abstract boolean isEmptyVisualization(ResultEntity<T, O> resultEntity);

    public String removeFileExtension(
            String filename,
            boolean removeAllExtensions) {
        String extensionsPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");

        return Optional.ofNullable(filename)
                .filter(f -> !f.isEmpty())
                .map(f -> f.replaceAll(extensionsPattern, ""))
                .orElseThrow(() -> {
                    logger.error("Filename not set exception occurred while removing the file extension.");

                    throw new FilenameNotSetException(
                            messageProvider.getMessage(MessageProvider.Message.FILENAME_NOT_SET));
                });
    }

    protected void checkDocumentExpiration(
            Instant createdAt,
            UUID id) {
        if ((int) Duration.between(createdAt, Instant.now()).toDays() >= documentStorageDays) {
            logger.error(String.format("Document with id '%s' expired, creation timestamp: [%s]", id, createdAt));

            throw new DocumentExpiredException(
                    messageProvider.getMessage(MessageProvider.Message.DOCUMENT_EXPIRED_FORMAT), id);
        }
    }

    protected String getFileContentToReanalyze(
            UUID id,
            String filename,
            boolean usePdb
    ) {
        if (usePdb) {
            return analyzedFileService.findPdbFile(removeFileExtension(filename, true));
        } else {
            return analyzedFileService.findAnalyzedFile(id);
        }
    }

    protected void saveMongoEntity(
            E mongoEntity
    ) {
        List<UUID> resultsIds = new ArrayList<>();

        for (ResultEntity<T, O> result : mongoEntity.getResults()) {
            resultRepository.save(result);
            resultsIds.add(result.getId());
        }

        AnalysisData analysisData = new AnalysisData.Builder()
                .withId(mongoEntity.getId())
                .withFilename(mongoEntity.getFilename())
                .withResults(resultsIds)
                .withCreatedAt(mongoEntity.getCreatedAt())
                .withUsePdb(mongoEntity.isUsePdb())
                .withScenario(scenario)
                .build();

        analysisDataRepository.save(analysisData);
    }

    protected void saveNewResultEntity(
            UUID id,
            ResultEntity<T, O> resultEntity
    ) {
        AnalysisData analysisData = findAnalysisDataDocument(id);
        analysisData.addResult(resultEntity.getId());
        analysisDataRepository.save(analysisData);
        resultRepository.save(resultEntity);
    }

    protected AnalysisData findAnalysisDataDocument(UUID id) {
        Optional<AnalysisData> optionalAnalysisData = analysisDataRepository.findById(id);

        if (optionalAnalysisData.isEmpty() || !optionalAnalysisData.get().getScenario().equals(scenario)) {
            logger.error(String.format("Current id '%s' not found.", id));
            throw new IdNotFoundException(messageProvider.getMessage(MessageProvider.Message.ID_NOT_FOUND_FORMAT), id);
        }

        return optionalAnalysisData.get();
    }

    protected ResultEntity<T, O> findResultEntityDocument(
            UUID resultId,
            UUID id
    ) {
        Optional<ResultEntity<T, O>> optionalResultEntity = resultRepository.findById(resultId);

        if (optionalResultEntity.isEmpty()) {
            logger.error(String.format("Current id '%s' not found.", id));
            throw new IdNotFoundException(messageProvider.getMessage(MessageProvider.Message.ID_NOT_FOUND_FORMAT), id);
        }

        return optionalResultEntity.get();
    }

    protected Optional<ResultEntity<T, O>> findExpiredResultEntityDocument(
            UUID resultId
    ) {
        Optional<ResultEntity<T, O>> optionalResultEntity = resultRepository.findById(resultId);

        if (optionalResultEntity.isEmpty()) {
            logger.warn(String.format("Expired result entity document with id: [%s] not found.", resultId));
            return Optional.empty();
        }

        return optionalResultEntity;
    }
}
