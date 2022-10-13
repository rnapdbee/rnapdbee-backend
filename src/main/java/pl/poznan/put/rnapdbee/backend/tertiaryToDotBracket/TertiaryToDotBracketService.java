package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketParamsEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.repository.TertiaryToDotBracketRepository;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

@Service
public class TertiaryToDotBracketService {

    private final TertiaryToDotBracketRepository tertiaryToDotBracketRepository;

    @Autowired
    private TertiaryToDotBracketService(TertiaryToDotBracketRepository tertiaryToDotBracketRepository) {
        this.tertiaryToDotBracketRepository = tertiaryToDotBracketRepository;
    }

    public void getResults(UUID id) {
        UUID randomId = UUID.randomUUID();
        tertiaryToDotBracketRepository.save(new TertiaryToDotBracketMongoEntity(
                randomId,
                "filename-test",
                new HashSet<>(Arrays.asList(new TertiaryToDotBracketResultEntity(
                        new TertiaryToDotBracketParamsEntity(
                                ModelSelection.FIRST,
                                AnalysisTool.FR3D_PYTHON,
                                NonCanonicalHandling.VISUALIZATION_ONLY,
                                true,
                                StructuralElementsHandling.USE_PSEUDOKNOTS,
                                VisualizationTool.VARNA
                        ),
                        "output"
                ))),
                Instant.now()
        ));

        System.out.println(randomId);
        System.out.println(tertiaryToDotBracketRepository.findById(randomId).get().getId());
    }
}
