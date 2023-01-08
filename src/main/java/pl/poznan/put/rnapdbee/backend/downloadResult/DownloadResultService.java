package pl.poznan.put.rnapdbee.backend.downloadResult;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection2D;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection3D;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelectionMulti;
import pl.poznan.put.rnapdbee.backend.downloadResult.exception.BadEntriesSelectionListSizeException;
import pl.poznan.put.rnapdbee.backend.downloadResult.exception.BadSelectionListSizeException;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.SecondaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.TertiaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.TertiaryToMultiSecondaryService;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationPath;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMulti;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryMongoEntity;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

@Service
public class DownloadResultService {

    private static final Logger logger = LoggerFactory.getLogger(DownloadResultService.class);

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

    public String download3DResult(
            UUID id,
            List<DownloadSelection3D> downloadSelection3DList,
            ZipOutputStream stream
    ) {
        return "";
    }

    public String download2DResult(
            UUID id,
            List<DownloadSelection2D> downloadSelection2DList,
            ZipOutputStream stream
    ) {
        SecondaryToDotBracketMongoEntity document2D = secondaryToDotBracketService.findDocument(id);

        String filename = secondaryToDotBracketService.removeFileExtension(document2D.getFilename(), true);
        List<Output2D<ImageInformationPath>> output2DList = document2D.getResults()
                .stream()
                .map(ResultEntity::getOutput)
                .collect(Collectors.toList());

        int resultsCount = output2DList.size();
        int selectionListCount = downloadSelection2DList.size();
        checkSelectionListSize(resultsCount, selectionListCount);

        String basicDirPrefix = prepareBasicDirPrefix(filename, resultsCount);

        for (int i = 0; i < output2DList.size(); i++) {
            DownloadSelection2D selection2D = downloadSelection2DList.get(i);

            if (selection2D.isStrands()
                    || selection2D.isBpSeq()
                    || selection2D.isCt()
                    || selection2D.isInteractions()
                    || selection2D.isStructuralElements()
                    || selection2D.isImageInformation()) {

                String dirPrefix = prepareResultDirPrefix(basicDirPrefix, i, resultsCount);
                zipComponent.zipOutput2D(output2DList.get(i), selection2D, dirPrefix + filename, stream);
            }
        }

        return prepareZipName(filename);
    }

    public String downloadMultiResult(
            UUID id,
            List<DownloadSelectionMulti> downloadSelectionMultiList,
            ZipOutputStream stream
    ) {
        TertiaryToMultiSecondaryMongoEntity documentMulti = tertiaryToMultiSecondaryService.findDocument(id);

        String filename = tertiaryToMultiSecondaryService.removeFileExtension(documentMulti.getFilename(), true);
        List<OutputMulti<ImageInformationPath, ConsensualVisualizationPath>> resultsOutputMultiList =
                documentMulti.getResults()
                        .stream()
                        .map(ResultEntity::getOutput)
                        .collect(Collectors.toList());

        int resultsCount = resultsOutputMultiList.size();
        int selectionListCount = downloadSelectionMultiList.size();
        checkSelectionListSize(resultsCount, selectionListCount);

        String basicDirPrefix = prepareBasicDirPrefix(filename, resultsCount);

        for (int i = 0; i < resultsOutputMultiList.size(); i++) {
            DownloadSelectionMulti selectionMulti = downloadSelectionMultiList.get(i);
            String dirPrefix = prepareResultDirPrefix(basicDirPrefix, i, resultsCount);

            if (selectionMulti.isConsensualVisualization()) {
                zipComponent.zipConsensualVisualization(
                        resultsOutputMultiList.get(i).getConsensualVisualization(),
                        dirPrefix + filename,
                        stream);
            }

            downloadMultiOutput2D(
                    resultsOutputMultiList.get(i).getEntries(),
                    selectionMulti.getEntries(),
                    i,
                    dirPrefix,
                    filename,
                    stream);
        }

        return prepareZipName(filename);
    }

    /**
     * Methods adding to ZIP file every select field of Output2D from entries list of one Multi scenario result
     *
     * @param entryList        list of entries from one Multi scenario result
     * @param selectionEntries selection of entries to ZIP from one Multi scenario result
     * @param resultNumber     number of result to download
     * @param dirPrefix        prefix of directory to save selected files
     * @param filename         name of analyzed file
     * @param stream           Zip Output stream storing selected data
     */
    private void downloadMultiOutput2D(
            List<OutputMultiEntry<ImageInformationPath>> entryList,
            List<DownloadSelectionMulti.SingleDownloadSelectionMulti> selectionEntries,
            int resultNumber,
            String dirPrefix,
            String filename,
            ZipOutputStream stream
    ) {
        int entriesCount = entryList.size();
        int singleDownloadSelectionMultiCount = selectionEntries.size();
        checkEntriesSelectionListSize(resultNumber, entriesCount, singleDownloadSelectionMultiCount);

        for (int j = 0; j < entriesCount; j++) {
            DownloadSelection2D selection2D = selectionEntries.get(j).getOutput2D();

            if (selection2D.isStrands()
                    || selection2D.isBpSeq()
                    || selection2D.isCt()
                    || selection2D.isInteractions()
                    || selection2D.isStructuralElements()
                    || selection2D.isImageInformation()) {

                String entriesDirPrefix = prepareResultDirPrefix(dirPrefix, j, entriesCount);
                zipComponent.zipOutput2D(entryList.get(j).getOutput2D(), selection2D, entriesDirPrefix + filename, stream);
            }
        }
    }

    private String prepareBasicDirPrefix(
            String filename,
            int resultsCount
    ) {
        if (resultsCount > 1)
            return filename + File.separator;
        else
            return "";
    }

    private String prepareResultDirPrefix(
            String basicDirPrefix,
            int resultsNumber,
            int resultsCount
    ) {
        if (resultsCount > 1)
            return basicDirPrefix + resultsNumber + File.separator;
        else
            return "";
    }

    private String prepareZipName(String filename) {
        return String.format("RNApdbee-%s-%s.zip",
                filename,
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()));
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
}
