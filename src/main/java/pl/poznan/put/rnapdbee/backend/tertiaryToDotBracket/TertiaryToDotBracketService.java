package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageUtils;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.Output3D;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.SingleTertiaryModelOutput;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.repository.TertiaryToDotBracketRepository;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.UUID;

import static pl.poznan.put.rnapdbee.backend.shared.domain.ValidationPolicy.validateFilename;

@Service
public class TertiaryToDotBracketService {

    private final TertiaryToDotBracketRepository tertiaryToDotBracketRepository;
    private final AnalyzedFileService analyzedFileService;
    private final WebClient engineWebClient;
    private final ServletContext servletContext;

    @Autowired
    private TertiaryToDotBracketService(
            TertiaryToDotBracketRepository tertiaryToDotBracketRepository,
            AnalyzedFileService analyzedFileService,
            @Autowired @Qualifier("engineWebClient") WebClient engineWebClient,
            ServletContext servletContext
    ) {
        this.tertiaryToDotBracketRepository = tertiaryToDotBracketRepository;
        this.analyzedFileService = analyzedFileService;
        this.engineWebClient = engineWebClient;
        this.servletContext = servletContext;
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

        String filename = validateFilename(contentDispositionHeader);

        EngineResponse3D engineResponse3D = performAnalysisOnEngine(
                modelSelection,
                analysisTool,
                nonCanonicalHandling,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                contentDispositionHeader,
                fileContent);

        Output3D.Builder<ImageInformationPath> output3DBuilder =
                new Output3D.Builder<ImageInformationPath>()
                        .withTitle(engineResponse3D.getTitle());

        for (SingleTertiaryModelOutput<ImageInformationByteArray> model : engineResponse3D.getModels()) {
            String pathToSVGImage = ImageUtils.generateSvgUrl(
                    servletContext,
                    model.getOutput2D().getImageInformation().getSvgFile()).getRight();

            output3DBuilder.addModel(
                    SingleTertiaryModelOutput.of(
                            model,
                            Output2D.of(
                                    model.getOutput2D(),
                                    ImageInformationPath.of(
                                            model.getOutput2D().getImageInformation(),
                                            pathToSVGImage)
                            )
                    )
            );
        }

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
                                output3DBuilder.build()
                        )
                );

        tertiaryToDotBracketRepository.save(tertiaryToDotBracketMongoEntity);
        analyzedFileService.saveAnalyzedFile(id, fileContent);

        return tertiaryToDotBracketMongoEntity;
    }

    private EngineResponse3D performAnalysisOnEngine(
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {
        return engineWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/3d")
                        .queryParam("modelSelection", modelSelection)
                        .queryParam("analysisTool", analysisTool)
                        .queryParam("nonCanonicalHandling", nonCanonicalHandling)
                        .queryParam("removeIsolated", removeIsolated)
                        .queryParam("structuralElementsHandling", structuralElementsHandling)
                        .queryParam("visualizationTool", visualizationTool)
                        .build())
                .header("Content-Disposition", contentDispositionHeader)
                .body(BodyInserters.fromValue(fileContent))
                .retrieve()
                .bodyToMono(EngineResponse3D.class)
                .block();
    }

    private static class EngineResponse3D extends Output3D<ImageInformationByteArray> {
        private EngineResponse3D(
                List<SingleTertiaryModelOutput<ImageInformationByteArray>> models,
                String title) {
            super(models, title);
        }
    }
}
