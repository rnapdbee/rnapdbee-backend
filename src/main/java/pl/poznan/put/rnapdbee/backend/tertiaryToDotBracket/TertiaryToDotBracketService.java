package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntity;
import pl.poznan.put.rnapdbee.backend.shared.EngineClient;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.ValidationComponent;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.exception.domain.IdNotFoundException;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.Output3D;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.SingleTertiaryModelOutput;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.repository.TertiaryToDotBracketRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class TertiaryToDotBracketService {

    private final TertiaryToDotBracketRepository tertiaryToDotBracketRepository;
    private final AnalyzedFileService analyzedFileService;
    private final EngineClient engineClient;
    private final ValidationComponent validationComponent;
    private final ImageComponent imageComponent;

    @Autowired
    private TertiaryToDotBracketService(
            TertiaryToDotBracketRepository tertiaryToDotBracketRepository,
            AnalyzedFileService analyzedFileService,
            EngineClient engineClient,
            ValidationComponent validationComponent,
            ImageComponent imageComponent
    ) {
        this.tertiaryToDotBracketRepository = tertiaryToDotBracketRepository;
        this.analyzedFileService = analyzedFileService;
        this.engineClient = engineClient;
        this.validationComponent = validationComponent;
        this.imageComponent = imageComponent;
    }

    public TertiaryToDotBracketMongoEntity analyzeTertiaryToDotBracket(
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {

        String filename = validationComponent.validateFilename(contentDispositionHeader);

        Output3D<ImageInformationByteArray> engineResponse3D = engineClient.perform3DAnalysisOnEngine(
                modelSelection,
                analysisTool,
                nonCanonicalHandling,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                contentDispositionHeader,
                fileContent);

        Output3D<ImageInformationPath> output3D = saveGraphicsWithPath(engineResponse3D);

        UUID id = IdSupplier.generateId();

        TertiaryToDotBracketMongoEntity tertiaryToDotBracketMongoEntity =
                TertiaryToDotBracketMongoEntity.of(
                        id,
                        filename,
                        ResultEntity.of(
                                TertiaryToDotBracketParams.of(
                                        modelSelection,
                                        analysisTool,
                                        nonCanonicalHandling,
                                        removeIsolated,
                                        structuralElementsHandling,
                                        visualizationTool
                                ),
                                output3D
                        )
                );

        tertiaryToDotBracketRepository.save(tertiaryToDotBracketMongoEntity);
        analyzedFileService.saveAnalyzedFile(id, fileContent);

        return tertiaryToDotBracketMongoEntity;
    }

    public TertiaryToDotBracketMongoEntity getResultsTertiaryToDotBracket(UUID id) {
        return findTertiaryToDotBracketDocument(id);
    }

    public TertiaryToDotBracketMongoEntity reanalyzeTertiaryToDotBracket(
            UUID id,
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool
    ) {
        AnalyzedFileEntity analyzedFile = analyzedFileService.findAnalyzedFile(id);
        TertiaryToDotBracketMongoEntity tertiaryToDotBracketMongoEntity = findTertiaryToDotBracketDocument(id);

        String contentDispositionHeader = ContentDisposition.builder("attachment")
                .filename(tertiaryToDotBracketMongoEntity.getFilename())
                .build()
                .toString();

        Output3D<ImageInformationByteArray> engineResponse3D = engineClient.perform3DAnalysisOnEngine(
                modelSelection,
                analysisTool,
                nonCanonicalHandling,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                contentDispositionHeader,
                analyzedFile.getContent());

        Output3D<ImageInformationPath> output3D = saveGraphicsWithPath(engineResponse3D);

        ResultEntity<TertiaryToDotBracketParams, Output3D<ImageInformationPath>> resultEntity =
                ResultEntity.of(
                        TertiaryToDotBracketParams.of(
                                modelSelection,
                                analysisTool,
                                nonCanonicalHandling,
                                removeIsolated,
                                structuralElementsHandling,
                                visualizationTool
                        ),
                        output3D
                );

        tertiaryToDotBracketMongoEntity.addResult(resultEntity);
        tertiaryToDotBracketRepository.save(tertiaryToDotBracketMongoEntity);

        return tertiaryToDotBracketMongoEntity;
    }

    private Output3D<ImageInformationPath> saveGraphicsWithPath(Output3D<ImageInformationByteArray> engineResponse3D) {
        Output3D.Builder<ImageInformationPath> output3DBuilder =
                new Output3D.Builder<ImageInformationPath>()
                        .withTitle(engineResponse3D.getTitle());

        for (SingleTertiaryModelOutput<ImageInformationByteArray> model : engineResponse3D.getModels()) {
            String pathToSVGImage = imageComponent.generateSvgUrl(model.getOutput2D().getImageInformation().getSvgFile());

            output3DBuilder.addModel(
                    SingleTertiaryModelOutput.of(
                            model,
                            Output2D.of(
                                    model.getOutput2D(),
                                    ImageInformationPath.of(
                                            model.getOutput2D().getImageInformation(),
                                            pathToSVGImage
                                    )
                            )
                    )
            );
        }

        return output3DBuilder.build();
    }

    private TertiaryToDotBracketMongoEntity findTertiaryToDotBracketDocument(UUID id) {
        Optional<TertiaryToDotBracketMongoEntity> tertiaryToDotBracketMongoEntity =
                tertiaryToDotBracketRepository.findById(id);

        if (tertiaryToDotBracketMongoEntity.isEmpty())
            throw new IdNotFoundException(id);

        return tertiaryToDotBracketMongoEntity.get();
    }
}
