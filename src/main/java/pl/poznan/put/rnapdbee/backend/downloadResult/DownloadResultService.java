package pl.poznan.put.rnapdbee.backend.downloadResult;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection2D;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection3D;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelectionMulti;
import pl.poznan.put.rnapdbee.backend.downloadResult.exception.BadEntriesSelectionListSizeException;
import pl.poznan.put.rnapdbee.backend.downloadResult.exception.BadModelsSelectionListSizeException;
import pl.poznan.put.rnapdbee.backend.downloadResult.exception.BadSelectionListSizeException;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.SecondaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.TertiaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.SingleTertiaryModelOutput;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.TertiaryToMultiSecondaryService;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryParams;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

@Service
public class DownloadResultService {

    private static final Logger logger = LoggerFactory.getLogger(DownloadResultService.class);
    private static final String DIR_DELIMITER = "-";
    private static final String REMOVE_ISOLATED_ARCHIVE_NAME = "Isolated_removed";
    private static final String INCLUDE_NON_CANONICAL_ARCHIVE_NAME = "Non_canonical_included";

    private final SecondaryToDotBracketService secondaryToDotBracketService;
    private final TertiaryToDotBracketService tertiaryToDotBracketService;
    private final TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService;
    private final ZipComponent zipComponent;
    private final MessageProvider messageProvider;

    public DownloadResultService(
            SecondaryToDotBracketService secondaryToDotBracketService,
            TertiaryToDotBracketService tertiaryToDotBracketService,
            TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService,
            ZipComponent zipComponent,
            MessageProvider messageProvider) {
        this.secondaryToDotBracketService = secondaryToDotBracketService;
        this.tertiaryToDotBracketService = tertiaryToDotBracketService;
        this.tertiaryToMultiSecondaryService = tertiaryToMultiSecondaryService;
        this.zipComponent = zipComponent;
        this.messageProvider = messageProvider;
    }

    public String download3DResults(
            UUID id,
            List<DownloadSelection3D> downloadSelection3DList,
            ZipOutputStream stream
    ) {
        TertiaryToDotBracketMongoEntity document3D = tertiaryToDotBracketService.findDocument(id);

        String filename = tertiaryToDotBracketService.removeFileExtension(
                document3D.getFilename(),
                true);
        String fileExtension = document3D.getFileExtension();

        int resultsCount = document3D.getResults().size();
        checkSelectionListSize(resultsCount, downloadSelection3DList.size());

        for (int resultNumber = 0; resultNumber < resultsCount; resultNumber++) {
            String resultDirPrefix = prepareResultDirPrefix(
                    document3D.getResults().get(resultNumber).getParams(),
                    resultsCount);

            download3DModels(
                    document3D.getResults().get(resultNumber).getOutput().getModels(),
                    downloadSelection3DList.get(resultNumber).getModels(),
                    resultNumber,
                    resultDirPrefix,
                    filename,
                    fileExtension,
                    stream);
        }

        return prepareZipName(filename);
    }

    public String download2DResults(
            UUID id,
            List<DownloadSelection2D> downloadSelection2DList,
            ZipOutputStream stream
    ) {
        SecondaryToDotBracketMongoEntity document2D = secondaryToDotBracketService.findDocument(id);

        String filename = secondaryToDotBracketService.removeFileExtension(
                document2D.getFilename(),
                true);
        String fileExtension = document2D.getFileExtension();

        int resultsCount = document2D.getResults().size();
        checkSelectionListSize(resultsCount, downloadSelection2DList.size());

        for (int resultNumber = 0; resultNumber < resultsCount; resultNumber++) {
            DownloadSelection2D selection2D = downloadSelection2DList.get(resultNumber);

            if (selection2D.isStrands()
                    || selection2D.isBpSeq()
                    || selection2D.isCt()
                    || selection2D.isInteractions()
                    || selection2D.isStructuralElements()
                    || selection2D.isImageInformation()) {

                String resultDirPrefix = prepareResultDirPrefix(
                        document2D.getResults().get(resultNumber).getParams(),
                        resultsCount);

                zipComponent.zipOutput2D(
                        document2D.getResults().get(resultNumber).getOutput(),
                        selection2D,
                        resultDirPrefix + filename,
                        fileExtension,
                        stream);
            }
        }

        return prepareZipName(filename);
    }

    public String downloadMultiResults(
            UUID id,
            List<DownloadSelectionMulti> downloadSelectionMultiList,
            ZipOutputStream stream
    ) {
        TertiaryToMultiSecondaryMongoEntity documentMulti = tertiaryToMultiSecondaryService.findDocument(id);

        String filename = tertiaryToMultiSecondaryService.removeFileExtension(
                documentMulti.getFilename(),
                true);
        String fileExtension = documentMulti.getFileExtension();

        int resultsCount = documentMulti.getResults().size();
        checkSelectionListSize(resultsCount, downloadSelectionMultiList.size());

        for (int resultNumber = 0; resultNumber < resultsCount; resultNumber++) {
            DownloadSelectionMulti selectionMulti = downloadSelectionMultiList.get(resultNumber);
            String resultDirPrefix = prepareResultDirPrefix(
                    documentMulti.getResults().get(resultNumber).getParams(),
                    resultsCount);

            if (selectionMulti.isConsensualVisualization()) {
                zipComponent.zipConsensualVisualization(
                        documentMulti.getResults().get(resultNumber).getOutput().getConsensualVisualization(),
                        resultDirPrefix + filename,
                        stream);
            }

            downloadMultiEntries(
                    documentMulti.getResults().get(resultNumber).getOutput().getEntries(),
                    selectionMulti.getEntries(),
                    resultNumber,
                    resultDirPrefix,
                    filename,
                    fileExtension,
                    stream);
        }

        return prepareZipName(filename);
    }

    private void download3DModels(
            List<SingleTertiaryModelOutput<ImageInformationPath>> modelsList,
            List<DownloadSelection3D.SingleDownloadSelection3D> selectionsModels,
            int resultNumber,
            String resultDirPrefix,
            String filename,
            String fileExtension,
            ZipOutputStream stream
    ) {
        int modelsCount = modelsList.size();
        checkModelsSelectionListSize(resultNumber + 1, modelsCount, selectionsModels.size());

        for (int modelNumber = 0; modelNumber < modelsCount; modelNumber++) {
            String modelDirPrefix = prepareModelDirPrefix(
                    resultDirPrefix,
                    modelsList.get(modelNumber).getModelNumber(),
                    modelsCount);

            zipComponent.zipSingleTertiaryModelOutput(
                    modelsList.get(modelNumber),
                    selectionsModels.get(modelNumber),
                    modelDirPrefix + filename,
                    fileExtension,
                    stream);
        }
    }

    /**
     * Methods adding to ZIP file every select field of Output2D from entries list of one Multi scenario result
     *
     * @param entriesList      list of entries from one Multi scenario result
     * @param selectionEntries selection of entries to ZIP from one Multi scenario result
     * @param resultNumber     number of result to download
     * @param resultDirPrefix  prefix of result directory to save selected files
     * @param filename         name of analyzed file
     * @param stream           Zip Output stream storing selected data
     */
    private void downloadMultiEntries(
            List<OutputMultiEntry<ImageInformationPath>> entriesList,
            List<DownloadSelectionMulti.SingleDownloadSelectionMulti> selectionEntries,
            int resultNumber,
            String resultDirPrefix,
            String filename,
            String fileExtension,
            ZipOutputStream stream
    ) {
        int entriesCount = entriesList.size();
        checkEntriesSelectionListSize(resultNumber + 1, entriesCount, selectionEntries.size());

        for (int entryNumber = 0; entryNumber < entriesCount; entryNumber++) {
            DownloadSelection2D selection2D = selectionEntries.get(entryNumber).getOutput2D();

            if (selection2D.isStrands()
                    || selection2D.isBpSeq()
                    || selection2D.isCt()
                    || selection2D.isInteractions()
                    || selection2D.isStructuralElements()
                    || selection2D.isImageInformation()) {

                String entriesDirPrefix = prepareEntryDirPrefix(
                        resultDirPrefix,
                        entriesList.get(entryNumber).getAdapterEnums(),
                        entryNumber,
                        entriesCount);

                zipComponent.zipOutput2D(
                        entriesList.get(entryNumber).getOutput2D(),
                        selection2D,
                        entriesDirPrefix + filename,
                        fileExtension,
                        stream);
            }
        }
    }

    private String prepareZipName(String filename) {
        return String.format("RNApdbee-%s-%s.zip",
                filename,
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()));
    }

    private String prepareResultDirPrefix(
            TertiaryToDotBracketParams params3D,
            int resultsCount
    ) {
        if (resultsCount > 1) {
            List<String> dirBuilder = new ArrayList<>();

            dirBuilder.add(params3D.getModelSelection().getArchiveName());
            dirBuilder.add(params3D.getAnalysisTool().getArchiveName());

            if (params3D.getNonCanonicalHandling() != NonCanonicalHandling.IGNORE)
                dirBuilder.add(params3D.getNonCanonicalHandling().getArchiveName());

            if (params3D.isRemoveIsolated())
                dirBuilder.add(REMOVE_ISOLATED_ARCHIVE_NAME);

            dirBuilder.add(params3D.getStructuralElementsHandling().getArchiveName());
            dirBuilder.add(params3D.getVisualizationTool().getArchiveName());

            return String.join(DIR_DELIMITER, dirBuilder) + File.separator;
        }
        return "";
    }

    private String prepareResultDirPrefix(
            SecondaryToDotBracketParams params2D,
            int resultsCount
    ) {
        if (resultsCount > 1) {
            List<String> dirBuilder = new ArrayList<>();

            dirBuilder.add(params2D.getStructuralElementsHandling().getArchiveName());

            if (params2D.isRemoveIsolated())
                dirBuilder.add(REMOVE_ISOLATED_ARCHIVE_NAME);

            dirBuilder.add(params2D.getVisualizationTool().getArchiveName());

            return String.join(DIR_DELIMITER, dirBuilder) + File.separator;
        }
        return "";
    }

    private String prepareResultDirPrefix(
            TertiaryToMultiSecondaryParams paramsMulti,
            int resultsCount
    ) {
        if (resultsCount > 1) {
            List<String> dirBuilder = new ArrayList<>();

            if (paramsMulti.isIncludeNonCanonical())
                dirBuilder.add(INCLUDE_NON_CANONICAL_ARCHIVE_NAME);

            if (paramsMulti.isRemoveIsolated())
                dirBuilder.add(REMOVE_ISOLATED_ARCHIVE_NAME);

            dirBuilder.add(paramsMulti.getVisualizationTool().getArchiveName());

            return String.join(DIR_DELIMITER, dirBuilder) + File.separator;
        }
        return "";
    }

    private String prepareModelDirPrefix(
            String resultDirPrefix,
            int modelNumber,
            int modelsCount
    ) {
        if (modelsCount > 1) {
            String modelDir = String.format("Model_%s", modelNumber);

            return resultDirPrefix + modelDir + File.separator;
        }
        return resultDirPrefix;
    }

    private String prepareEntryDirPrefix(
            String resultDirPrefix,
            List<AnalysisTool> analysisTools,
            int entryNumber,
            int entriesCount
    ) {
        if (entriesCount > 1) {
            String toolsArchiveNames = analysisTools.stream()
                    .map(AnalysisTool::getArchiveName)
                    .collect(Collectors.joining(DIR_DELIMITER));
            String entryDir = String.format("Entry_%s%s%s", entryNumber, DIR_DELIMITER, toolsArchiveNames);

            return resultDirPrefix + entryDir + File.separator;
        }
        return resultDirPrefix;
    }

    private void checkSelectionListSize(
            int resultsCount,
            int selectionListCount) {
        if (resultsCount != selectionListCount) {
            logger.error(String.format("Download selection list size incorrect, expected: [%s], occurred: [%s].",
                    resultsCount,
                    selectionListCount));
            throw new BadSelectionListSizeException(
                    messageProvider.getMessage(MessageProvider.Message.BAD_SELECTION_LIST_SIZE_FORMAT),
                    resultsCount,
                    selectionListCount);
        }
    }

    private void checkEntriesSelectionListSize(
            int resultNumber,
            int entriesCount,
            int singleDownloadSelectionMultiCount) {
        if (entriesCount != singleDownloadSelectionMultiCount) {
            logger.error(String.format("Download selection list [%s] element entries size incorrect, expected: [%s], occurred: [%s].",
                    resultNumber,
                    entriesCount,
                    singleDownloadSelectionMultiCount));
            throw new BadEntriesSelectionListSizeException(
                    messageProvider.getMessage(MessageProvider.Message.BAD_ENTRIES_SELECTION_LIST_SIZE_FORMAT),
                    resultNumber,
                    entriesCount,
                    singleDownloadSelectionMultiCount);
        }
    }

    private void checkModelsSelectionListSize(
            int resultNumber,
            int entriesCount,
            int singleDownloadSelectionMultiCount) {
        if (entriesCount != singleDownloadSelectionMultiCount) {
            logger.error(String.format("Download selection list [%s] element models size incorrect, expected: [%s], occurred: [%s].",
                    resultNumber,
                    entriesCount,
                    singleDownloadSelectionMultiCount));
            throw new BadModelsSelectionListSizeException(
                    messageProvider.getMessage(MessageProvider.Message.BAD_MODELS_SELECTION_LIST_SIZE_FORMAT),
                    resultNumber,
                    entriesCount,
                    singleDownloadSelectionMultiCount);
        }
    }
}
