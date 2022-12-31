package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.SecondaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.BaseController;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

import java.util.UUID;

/**
 * Controller class for the Secondary To Dot Bracket API.
 */
@RestController
@RequestMapping("api/v1/engine/2d")
public class SecondaryToDotBracketController extends BaseController {

    private final SecondaryToDotBracketService secondaryToDotBracketService;

    @Autowired
    private SecondaryToDotBracketController(
            MessageProvider messageProvider,
            SecondaryToDotBracketService secondaryToDotBracketService
    ) {
        super(messageProvider);
        this.secondaryToDotBracketService = secondaryToDotBracketService;
    }

    @Operation(summary = "Perform a 2D to Dot-Bracket calculation")
    @PostMapping(produces = "application/json", consumes = "text/plain")
    public SecondaryToDotBracketMongoEntity calculateSecondaryToDotBracket(
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestHeader("Content-Disposition") String contentDispositionHeader,
            @RequestBody String fileContent
    ) {
        logger.info(String.format("Analyze 2D -> (...) for content-disposition header: [%s] with params: [" +
                        "removeIsolated = %s, " +
                        "structuralElementsHandling = %s, " +
                        "visualizationTool = %s]",
                contentDispositionHeader,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool));

        String filename = validateContentDisposition(contentDispositionHeader);
        return secondaryToDotBracketService.analyzeSecondaryToDotBracket(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                filename,
                fileContent);
    }

    @Operation(summary = "Fetch a 2D calculation from the database")
    @GetMapping(path = "/{id}", produces = "application/json")
    public SecondaryToDotBracketMongoEntity getResultSecondaryToDotBracket(
            @PathVariable("id") UUID id
    ) {
        logger.info(String.format("Fetch 2D -> (...) results with id: [%s]", id));

        return secondaryToDotBracketService.getResultsSecondaryToDotBracket(id);
    }

    @Operation(summary = "Reanalyze calculation with different parameters")
    @PostMapping(path = "/{id}", produces = "application/json")
    public SecondaryToDotBracketMongoEntity reanalyzeSecondaryToDotBracket(
            @PathVariable("id") UUID id,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool
    ) {
        logger.info(String.format("Reanalyze 2D -> (...) for id: [%s] with params: [" +
                        "removeIsolated = %s, " +
                        "structuralElementsHandling = %s, " +
                        "visualizationTool = %s]",
                id,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool));

        return secondaryToDotBracketService.reanalyzeSecondaryToDotBracket(
                id,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool);
    }
}
