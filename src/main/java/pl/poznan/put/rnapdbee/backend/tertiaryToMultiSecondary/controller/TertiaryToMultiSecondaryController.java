package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.rnapdbee.backend.shared.BaseController;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.TertiaryToMultiSecondaryService;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryMongoEntity;

import java.util.UUID;

/**
 * Controller class for the Tertiary To Multi Secondary API.
 */
@RestController
@RequestMapping("api/v1/engine/multi")
public class TertiaryToMultiSecondaryController extends BaseController {

    private final TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService;

    @Autowired
    private TertiaryToMultiSecondaryController(
            MessageProvider messageProvider,
            Logger logger,
            TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService
    ) {
        super(messageProvider, logger);
        this.tertiaryToMultiSecondaryService = tertiaryToMultiSecondaryService;
    }

    @Operation(summary = "Perform a 3D to multi 2D calculation")
    @PostMapping(produces = "application/json", consumes = "text/plain")
    public TertiaryToMultiSecondaryMongoEntity calculateTertiaryToMultiSecondary(
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestHeader("Content-Disposition") String contentDispositionHeader,
            @RequestBody String fileContent
    ) {
        logger.info(String.format("Analyze 3D -> multi 2D for content-disposition header: [%s] with params: [" +
                        "includeNonCanonical = %s, " +
                        "removeIsolated = %s, " +
                        "visualizationTool = %s]",
                contentDispositionHeader,
                includeNonCanonical,
                removeIsolated,
                visualizationTool));

        String filename = validateContentDisposition(contentDispositionHeader);
        return tertiaryToMultiSecondaryService.analyzeTertiaryToMultiSecondary(
                includeNonCanonical,
                removeIsolated,
                visualizationTool,
                filename,
                fileContent);
    }

    @Operation(summary = "Fetch an existing multi calculation")
    @GetMapping(path = "/{id}", produces = "application/json")
    public TertiaryToMultiSecondaryMongoEntity getResultTertiaryToMultiSecondary(
            @PathVariable("id") UUID id
    ) {
        logger.info(String.format("Fetch 3D -> multi 2D results with id: [%s]", id));

        return tertiaryToMultiSecondaryService.getResultsTertiaryToMultiSecondary(id);
    }

    @Operation(summary = "Reanalyze calculation with different parameters")
    @PostMapping(path = "/{id}", produces = "application/json")
    public TertiaryToMultiSecondaryMongoEntity reanalyzeTertiaryToMultiSecondary(
            @PathVariable("id") UUID id,
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool
    ) {
        logger.info(String.format("Reanalyze 3D -> multi 2D for id: [%s] with params: [" +
                        "includeNonCanonical = %s, " +
                        "removeIsolated = %s, " +
                        "visualizationTool = %s]",
                id,
                includeNonCanonical,
                removeIsolated,
                visualizationTool));

        return tertiaryToMultiSecondaryService.reanalyzeTertiaryToMultiSecondary(
                id,
                includeNonCanonical,
                removeIsolated,
                visualizationTool);
    }

    @Operation(summary = "Perform a multi based on object fetched from Protein Data Bank")
    @PostMapping(path = "/pdb/{pdbId}")
    public TertiaryToMultiSecondaryMongoEntity calculatePDBTertiaryToMultiSecondary(
            @PathVariable("pdbId") String pdbId,
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool
    ) {
        logger.info(String.format("PDB Analyze 3D -> multi 2D for PDBId: [%s]; with params: [" +
                        "includeNonCanonical = %s, " +
                        "removeIsolated = %s, " +
                        "visualizationTool = %s]",
                pdbId,
                includeNonCanonical,
                removeIsolated,
                visualizationTool));

        return tertiaryToMultiSecondaryService.analyzePdbTertiaryToMultiSecondary(
                pdbId,
                includeNonCanonical,
                removeIsolated,
                visualizationTool);
    }
}
