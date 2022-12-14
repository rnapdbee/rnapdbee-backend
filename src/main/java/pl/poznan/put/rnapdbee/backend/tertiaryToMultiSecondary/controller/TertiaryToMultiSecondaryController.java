package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.TertiaryToMultiSecondaryService;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryMongoEntity;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/engine/multi")
public class TertiaryToMultiSecondaryController {

    private final TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService;

    @Autowired
    private TertiaryToMultiSecondaryController(TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService) {
        this.tertiaryToMultiSecondaryService = tertiaryToMultiSecondaryService;
    }

    @Operation(summary = "Perform a 3D to multi 2D calculation")
    @PostMapping(produces = "application/json", consumes = "text/plain")
    public TertiaryToMultiSecondaryMongoEntity calculateTertiaryToMultiSecondary(
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestHeader("Content-Disposition") String contentDispositionHeader,
            @RequestBody String fileContent) {
        return tertiaryToMultiSecondaryService.analyzeTertiaryToMultiSecondary(
                includeNonCanonical,
                removeIsolated,
                visualizationTool,
                contentDispositionHeader,
                fileContent);
    }

    @Operation(summary = "Fetch an existing multi calculation")
    @GetMapping(path = "/{id}", produces = "application/json")
    public TertiaryToMultiSecondaryMongoEntity getResultTertiaryToMultiSecondary(
            @PathVariable("id") UUID id) {
        return tertiaryToMultiSecondaryService.getResultsTertiaryToMultiSecondary(id);
    }

    @Operation(summary = "Reanalyze calculation with different parameters")
    @PostMapping(path = "/{id}", produces = "application/json")
    public TertiaryToMultiSecondaryMongoEntity reanalyzeTertiaryToMultiSecondary(
            @PathVariable("id") UUID id,
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool) {
        return tertiaryToMultiSecondaryService.reanalyzeTertiaryToMultiSecondary(
                id,
                includeNonCanonical,
                removeIsolated,
                visualizationTool);
    }

    @Operation(summary = "Perform a multi based on object fetched from Protein Data Bank")
    @PostMapping(path = "/pdb/{pdbId}", produces = "application/json")
    public ResponseEntity<Object> calculatePDBTertiaryToMultiSecondary(
            @PathVariable("pdbId") String pdbId,
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool) {
        throw new UnsupportedOperationException();
    }
}
