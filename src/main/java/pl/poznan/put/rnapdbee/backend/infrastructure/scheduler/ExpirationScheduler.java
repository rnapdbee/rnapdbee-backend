package pl.poznan.put.rnapdbee.backend.infrastructure.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.SecondaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.AnalysisData;
import pl.poznan.put.rnapdbee.backend.shared.repository.AnalysisDataRepository;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.TertiaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.TertiaryToMultiSecondaryService;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@EnableScheduling
public class ExpirationScheduler {

    private final AnalyzedFileService analyzedFileService;
    private final AnalysisDataRepository analysisDataRepository;
    private final SecondaryToDotBracketService secondaryToDotBracketService;
    private final TertiaryToDotBracketService tertiaryToDotBracketService;
    private final TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService;
    private static final Logger logger = LoggerFactory.getLogger(ExpirationScheduler.class);
    @Value("${document.storage.days}")
    private int documentStorageDays;

    public ExpirationScheduler(
            AnalyzedFileService analyzedFileService,
            AnalysisDataRepository analysisDataRepository,
            SecondaryToDotBracketService secondaryToDotBracketService,
            TertiaryToDotBracketService tertiaryToDotBracketService,
            TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService) {
        this.analyzedFileService = analyzedFileService;
        this.analysisDataRepository = analysisDataRepository;
        this.secondaryToDotBracketService = secondaryToDotBracketService;
        this.tertiaryToDotBracketService = tertiaryToDotBracketService;
        this.tertiaryToMultiSecondaryService = tertiaryToMultiSecondaryService;
    }

    @Scheduled(cron = "${expired.results.cleaner.cron}")
    public void runExpirationScheduler() {
        logger.info(String.format("Cleaning expired results, expiration days: [%s]", documentStorageDays));
        analyzedFileService.deleteExpiredPdbFiles();

        List<AnalysisData> analysisDataList = analysisDataRepository.findAll(Sort.by("createdAt"));
        List<UUID> expiredAnalysisDataIds = new ArrayList<>();

        for (AnalysisData analysis : analysisDataList) {
            if ((int) Duration.between(analysis.getCreatedAt(), Instant.now()).toDays() >= documentStorageDays) {
                UUID expiredAnalysisId = analysis.getId();
                expiredAnalysisDataIds.add(expiredAnalysisId);
                analyzedFileService.deleteAnalyzedFile(expiredAnalysisId);

                switch (analysis.getScenario()) {
                    case SCENARIO_2D:
                        secondaryToDotBracketService.deleteExpiredResults(analysis.getResults());
                        break;
                    case SCENARIO_3D:
                        tertiaryToDotBracketService.deleteExpiredResults(analysis.getResults());
                        break;
                    case SCENARIO_MULTI:
                        tertiaryToMultiSecondaryService.deleteExpiredResults(analysis.getResults());
                        break;
                }
            } else {
                break;
            }
        }

        analysisDataRepository.deleteAllById(expiredAnalysisDataIds);
        logger.info(String.format("[%s] analysis results deleted", expiredAnalysisDataIds.size()));
    }
}
