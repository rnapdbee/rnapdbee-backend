package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.controller;

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
import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.TertiaryToDotBracketService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/engine/3d")
public class TertiaryToDotBracketController {

    private final TertiaryToDotBracketService tertiaryToDotBracketService;

    @Autowired
    private TertiaryToDotBracketController(TertiaryToDotBracketService tertiaryToDotBracketService) {
        this.tertiaryToDotBracketService = tertiaryToDotBracketService;
    }

    @Operation(summary = "Perform a 3d calculation based on the payload")
    @PostMapping(path = "/", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Object> calculateTertiaryToDotBracket(
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("analysisTool") AnalysisTool analysisTool,
            @RequestParam("nonCanonicalHandling") NonCanonicalHandling nonCanonicalHandling,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestHeader("Content-Disposition") String contentDispositionHeader,
            @RequestBody String fileContent) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Fetch an existing 3d calculation")
    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<Object> getResultTertiaryToDotBracket(
            @PathVariable("id") UUID id) {
        tertiaryToDotBracketService.getResults(id);
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Reanalyze calculation with different parameters")
    @PostMapping(path = "/{id}", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Object> reanalyzeTertiaryToDotBracket(
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("analysisTool") AnalysisTool analysisTool,
            @RequestParam("nonCanonicalHandling") NonCanonicalHandling nonCanonicalHandling,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @PathVariable("id") UUID id) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Perform a 3d calculation based on object fetched from Protein Data Bank")
    @PostMapping(path = "/pdb/{pdbId}", produces = "application/json")
    public ResponseEntity<Object> calculatePDBTertiaryToDotBracket(
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("analysisTool") AnalysisTool analysisTool,
            @RequestParam("nonCanonicalHandling") NonCanonicalHandling nonCanonicalHandling,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @PathVariable("pdbId") String pdbId) {
        throw new UnsupportedOperationException();
    }
}
