package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbFileEntity;
import pl.poznan.put.rnapdbee.backend.shared.BaseAnalyzeService;
import pl.poznan.put.rnapdbee.backend.shared.EngineClient;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.exception.IdNotFoundException;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.Output3D;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.SingleTertiaryModelOutput;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.repository.TertiaryToDotBracketRepository;

import java.util.Optional;
import java.util.UUID;

import static pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService.PDB_FILE_EXTENSION;

@Service
public class TertiaryToDotBracketService extends BaseAnalyzeService {

    private final TertiaryToDotBracketRepository tertiaryToDotBracketRepository;
    private final EngineClient engineClient;
    private final ImageComponent imageComponent;

    @Autowired
    private TertiaryToDotBracketService(
            TertiaryToDotBracketRepository tertiaryToDotBracketRepository,
            EngineClient engineClient,
            ImageComponent imageComponent,
            AnalyzedFileService analyzedFileService
    ) {
        super(analyzedFileService);
        this.tertiaryToDotBracketRepository = tertiaryToDotBracketRepository;
        this.engineClient = engineClient;
        this.imageComponent = imageComponent;
    }

    public TertiaryToDotBracketMongoEntity analyzeTertiaryToDotBracket(
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String filename,
            String fileContent) {

        Output3D<ImageInformationByteArray> engineResponse3D = engineClient.perform3DAnalysisOnEngine(
                modelSelection,
                analysisTool,
                nonCanonicalHandling,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                filename,
                fileContent);

        Output3D<ImageInformationPath> output3D = saveGraphicsWithPath(
                engineResponse3D,
                visualizationTool);

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
                        ),
                        false
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
        TertiaryToDotBracketMongoEntity tertiaryToDotBracketMongoEntity = findTertiaryToDotBracketDocument(id);
        checkDocumentExpiration(tertiaryToDotBracketMongoEntity.getCreatedAt(), id);

        String fileContent = getFileContentToReanalyze(
                id,
                tertiaryToDotBracketMongoEntity.getFilename(),
                tertiaryToDotBracketMongoEntity.isUsePdb());

        Output3D<ImageInformationByteArray> engineResponse3D = engineClient.perform3DAnalysisOnEngine(
                modelSelection,
                analysisTool,
                nonCanonicalHandling,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                tertiaryToDotBracketMongoEntity.getFilename(),
                fileContent);

        Output3D<ImageInformationPath> output3D = saveGraphicsWithPath(
                engineResponse3D,
                visualizationTool);

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

    public TertiaryToDotBracketMongoEntity analyzePdbTertiaryToDotBracket(
            String pdbIdLowercase,
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool
    ) {
        String pdbId = pdbIdLowercase.toUpperCase();

        PdbFileEntity pdbFile = analyzedFileService.fetchPdbStructure(pdbId);
        String filename = pdbId + PDB_FILE_EXTENSION;

        Output3D<ImageInformationByteArray> engineResponse3D = engineClient.perform3DAnalysisOnEngine(
                modelSelection,
                analysisTool,
                nonCanonicalHandling,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                filename,
                pdbFile.getContent());

        Output3D<ImageInformationPath> output3D = saveGraphicsWithPath(
                engineResponse3D,
                visualizationTool);

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
                        ),
                        true
                );

        tertiaryToDotBracketRepository.save(tertiaryToDotBracketMongoEntity);

        return tertiaryToDotBracketMongoEntity;
    }

    private Output3D<ImageInformationPath> saveGraphicsWithPath(
            Output3D<ImageInformationByteArray> engineResponse3D,
            VisualizationTool visualizationTool
    ) {
        Output3D.Builder<ImageInformationPath> output3DBuilder =
                new Output3D.Builder<ImageInformationPath>()
                        .withTitle(engineResponse3D.getTitle());

        for (SingleTertiaryModelOutput<ImageInformationByteArray> model : engineResponse3D.getModels()) {
            String pathToSVGImage;

            if (visualizationTool == VisualizationTool.NONE)
                pathToSVGImage = null;
            else
                pathToSVGImage = imageComponent.generateSvgUrl(model.getOutput2D().getImageInformation().getSvgFile());

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
