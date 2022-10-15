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
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.TertiaryToMultiSecondaryService;

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
    @PostMapping(path = "/", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Object> calculateTertiaryToMultiSecondary(
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestHeader("Content-Disposition") String contentDispositionHeader,
            @RequestBody String fileContent) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Fetch an existing multi calculation")
    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<Object> getResultTertiaryToMultiSecondary(
            @PathVariable("id") UUID id) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Reanalyze calculation with different parameters")
    @PostMapping(path = "/{id}", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Object> reanalyzeTertiaryToMultiSecondary(
            @PathVariable("id") UUID id,
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Perform a multi based on object fetched from Protein Data Bank")
    @PostMapping(path = "/pdb/{pdbId}", produces = "application/json")
    public ResponseEntity<Object> calculatePDBTertiaryToMultiSecondary(
            @PathVariable("pdbId") String pdbId,
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool) {
        throw new UnsupportedOperationException();
    }
}