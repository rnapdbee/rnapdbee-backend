package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.repository.SecondaryToDotBracketRepository;
import pl.poznan.put.rnapdbee.backend.shared.EngineClient;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.BaseAnalyzeService;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.exception.IdNotFoundException;

import java.util.Optional;
import java.util.UUID;

@Service
public class SecondaryToDotBracketService extends BaseAnalyzeService {
    private final SecondaryToDotBracketRepository secondaryToDotBracketRepository;
    private final EngineClient engineClient;
    private final ImageComponent imageComponent;

    @Autowired
    private SecondaryToDotBracketService(
            SecondaryToDotBracketRepository secondaryToDotBracketRepository,
            EngineClient engineClient,
            ImageComponent imageComponent,
            AnalyzedFileService analyzedFileService
    ) {
        super(analyzedFileService);
        this.secondaryToDotBracketRepository = secondaryToDotBracketRepository;
        this.engineClient = engineClient;
        this.imageComponent = imageComponent;
    }

    public SecondaryToDotBracketMongoEntity analyzeSecondaryToDotBracket(
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {

        String filename = validateContentDisposition(contentDispositionHeader);

        Output2D<ImageInformationByteArray> engineOutput2DResponse = engineClient.perform2DAnalysisOnEngine(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                contentDispositionHeader,
                fileContent);

        String pathToSVGImage = imageComponent.generateSvgUrl(engineOutput2DResponse.getImageInformation().getSvgFile());

        UUID id = IdSupplier.generateId();

        SecondaryToDotBracketMongoEntity secondaryToDotBracketMongoEntity =
                SecondaryToDotBracketMongoEntity.of(
                        id,
                        filename,
                        ResultEntity.of(
                                SecondaryToDotBracketParams.of(
                                        removeIsolated,
                                        structuralElementsHandling,
                                        visualizationTool
                                ),
                                Output2D.of(
                                        engineOutput2DResponse,
                                        ImageInformationPath.of(
                                                engineOutput2DResponse.getImageInformation(),
                                                pathToSVGImage)
                                )
                        )
                );

        secondaryToDotBracketRepository.save(secondaryToDotBracketMongoEntity);
        analyzedFileService.saveAnalyzedFile(id, fileContent);

        return secondaryToDotBracketMongoEntity;
    }

    public SecondaryToDotBracketMongoEntity getResultsSecondaryToDotBracket(UUID id) {
        return findSecondaryToDotBracketDocument(id);
    }

    public SecondaryToDotBracketMongoEntity reanalyzeSecondaryToDotBracket(
            UUID id,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool) {

        AnalyzedFileEntity analyzedFile = analyzedFileService.findAnalyzedFile(id);
        SecondaryToDotBracketMongoEntity secondaryToDotBracketMongoEntity = findSecondaryToDotBracketDocument(id);
        checkDocumentExpiration(secondaryToDotBracketMongoEntity.getCreatedAt(), id);

        String contentDispositionHeader = contentDispositionHeaderBuilder(
                secondaryToDotBracketMongoEntity.getFilename());

        Output2D<ImageInformationByteArray> engineOutput2DResponse = engineClient.perform2DAnalysisOnEngine(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                contentDispositionHeader,
                analyzedFile.getContent());

        String pathToSVGImage = imageComponent.generateSvgUrl(engineOutput2DResponse.getImageInformation().getSvgFile());

        ResultEntity<SecondaryToDotBracketParams, Output2D<ImageInformationPath>> resultEntity =
                ResultEntity.of(
                        SecondaryToDotBracketParams.of(
                                removeIsolated,
                                structuralElementsHandling,
                                visualizationTool
                        ),
                        Output2D.of(
                                engineOutput2DResponse,
                                ImageInformationPath.of(
                                        engineOutput2DResponse.getImageInformation(),
                                        pathToSVGImage)
                        )
                );

        secondaryToDotBracketMongoEntity.addResult(resultEntity);
        secondaryToDotBracketRepository.save(secondaryToDotBracketMongoEntity);

        return secondaryToDotBracketMongoEntity;
    }

    private SecondaryToDotBracketMongoEntity findSecondaryToDotBracketDocument(UUID id) {
        Optional<SecondaryToDotBracketMongoEntity> secondaryToDotBracketMongoEntity =
                secondaryToDotBracketRepository.findById(id);

        if (secondaryToDotBracketMongoEntity.isEmpty())
            throw new IdNotFoundException(id);

        return secondaryToDotBracketMongoEntity.get();
    }
}
