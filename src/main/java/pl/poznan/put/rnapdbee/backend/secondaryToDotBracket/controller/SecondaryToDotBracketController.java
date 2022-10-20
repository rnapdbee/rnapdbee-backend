package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.controller;

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
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.SecondaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/engine/2d")
public class SecondaryToDotBracketController {

    private final SecondaryToDotBracketService secondaryToDotBracketService;

    @Autowired
    private SecondaryToDotBracketController(SecondaryToDotBracketService secondaryToDotBracketService) {
        this.secondaryToDotBracketService = secondaryToDotBracketService;
    }

    @Operation(summary = "Perform a 2D to Dot-Bracket calculation")
    @PostMapping(path = "/", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Object> calculateSecondaryToDotBracket(
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestHeader("Content-Disposition") String contentDispositionHeader,
            @RequestBody String fileContent) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Fetch a 2D calculation from the database")
    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<Object> getResultSecondaryToDotBracket(
            @PathVariable("id") UUID id) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Reanalyze calculation with different parameters")
    @PostMapping(path = "/{id}", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Object> reanalyzeSecondaryToDotBracket(
            @PathVariable("id") UUID id,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool) {
        throw new UnsupportedOperationException();
    }
}
