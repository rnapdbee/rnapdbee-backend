package pl.poznan.put.rnapdbee.backend.downloadResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection2D;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection3D;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelectionMulti;
import pl.poznan.put.rnapdbee.backend.downloadResult.exception.BadEntriesSelectionListSizeException;
import pl.poznan.put.rnapdbee.backend.downloadResult.exception.BadSelectionListSizeException;
import pl.poznan.put.rnapdbee.backend.images.ImageComponent;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.SecondaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.SingleStrand;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.StructuralElement;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.TertiaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.TertiaryToMultiSecondaryService;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationPath;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMulti;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryMongoEntity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DownloadResultService {

    private static final Logger logger = LoggerFactory.getLogger(DownloadResultService.class);

    private static final String strandsPostfix = "-2D-dotbracket.txt";
    private static final String bpSeqPostfix = "-2D-bpseq.txt";
    private static final String ctPostfix = "-2D-ct.txt";
    private static final String interactionsPostfix = "-rna-to-rna.txt";
    private static final String structuralElementsPostfix = "-elements.txt";
    private static final String coordinatesPostfix = "-coordinates.txt";
    private static final String image2DPostfix = "-2D";
    private static final String imageConsensusPostfix = "-consensus";
    private static final String imageExtension = ".svg";

    private final SecondaryToDotBracketService secondaryToDotBracketService;
    private final TertiaryToDotBracketService tertiaryToDotBracketService;
    private final TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService;
    private final ImageComponent imageComponent;
    private final MessageProvider messageProvider;

    public DownloadResultService(
            SecondaryToDotBracketService secondaryToDotBracketService,
            TertiaryToDotBracketService tertiaryToDotBracketService,
            TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService,
            ImageComponent imageComponent,
            MessageProvider messageProvider) {
        this.secondaryToDotBracketService = secondaryToDotBracketService;
        this.tertiaryToDotBracketService = tertiaryToDotBracketService;
        this.tertiaryToMultiSecondaryService = tertiaryToMultiSecondaryService;
        this.imageComponent = imageComponent;
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
                zipOutput2D(output2DList.get(i), selection2D, dirPrefix + filename, stream);
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
                downloadConsensualVisualization(
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
                zipOutput2D(entryList.get(j).getOutput2D(), selection2D, entriesDirPrefix + filename, stream);
            }
        }
    }

    private void downloadConsensualVisualization(
            final ConsensualVisualizationPath consensualVisualizationPath,
            final String namePrefix,
            final ZipOutputStream zipOutputStream
    ) {
        String pathToSVGImage = consensualVisualizationPath.getPathToSVGImage();
        if (consensualVisualizationPath.getPathToSVGImage() != null)
            zipImage(pathToSVGImage, namePrefix, imageConsensusPostfix, zipOutputStream);
        else
            logger.error("Failed to archive not exist Consensus Visualization image.");
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

    private void zipOutput2D(
            final Output2D<ImageInformationPath> output2D,
            final DownloadSelection2D downloadSelection2D,
            final String namePrefix,
            final ZipOutputStream zipOutputStream
    ) {
        if (downloadSelection2D.isStrands())
            zipStrands(output2D.getStrands(), namePrefix, zipOutputStream);

        if (downloadSelection2D.isBpSeq())
            zipBpSeq(output2D.getBpSeq(), namePrefix, zipOutputStream);

        if (downloadSelection2D.isCt())
            zipCt(output2D.getCt(), namePrefix, zipOutputStream);

        if (downloadSelection2D.isInteractions())
            zipInteractions(output2D.getInteractions(), namePrefix, zipOutputStream);

        if (downloadSelection2D.isStructuralElements())
            zipStructuralElement(output2D.getStructuralElements(), namePrefix, zipOutputStream);

        if (downloadSelection2D.isImageInformation() && output2D.getImageInformation().wasDrawn())
            zipImage(output2D.getImageInformation().getPathToSVGImage(), namePrefix, image2DPostfix, zipOutputStream);
    }

    private void zipData(
            String data,
            String filename,
            ZipOutputStream zipOutputStream
    ) {
        try {
            zipOutputStream.putNextEntry(new ZipEntry(filename));
            IOUtils.write(data, zipOutputStream, Charset.defaultCharset());
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            logger.error(String.format("Failed to add [%s] file to ZIP archive.", filename), e);
        }
    }

    private void zipImage(
            String pathToSVGImage,
            String namePrefix,
            String namePostfix,
            ZipOutputStream zipOutputStream
    ) {
        FileSystemResource resource = imageComponent.findSvgImage(pathToSVGImage);
        String imageName = namePrefix + namePostfix + imageExtension;
        try {
            zipOutputStream.putNextEntry(new ZipEntry(imageName));
            zipOutputStream.write(resource.getInputStream().readAllBytes());
            zipOutputStream.closeEntry();
        } catch (final IOException e) {
            logger.error(String.format("Failed to add [%s] image file to ZIP archive", imageName), e);
        }
    }

    private void zipStrands(
            List<SingleStrand> strands,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (strands != null && !strands.isEmpty())
            zipData(strandsZipFormat(strands), namePrefix + strandsPostfix, zipOutputStream);
        else
            logger.error("Failed to archive not exist Strands data.");

    }

    private String strandsZipFormat(List<SingleStrand> strands) {
        return strands.stream()
                .map(SingleStrand::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private void zipBpSeq(
            List<String> bpSeq,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (bpSeq != null && !bpSeq.isEmpty())
            zipData(bpSeqZipFormat(bpSeq), namePrefix + bpSeqPostfix, zipOutputStream);
        else
            logger.error("Failed to archive not exist BPSEQ data.");

    }

    private String bpSeqZipFormat(List<String> bpSeq) {
        return bpSeq.stream()
                .map(seq -> seq + System.lineSeparator())
                .collect(Collectors.joining());
    }

    private void zipCt(
            List<String> ct,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (ct != null && !ct.isEmpty())
            zipData(ctZipFormat(ct), namePrefix + ctPostfix, zipOutputStream);
        else
            logger.error("Failed to archive not exist CT data.");
    }

    private String ctZipFormat(List<String> ct) {
        return ct.stream()
                .map(line -> line + System.lineSeparator())
                .collect(Collectors.joining());
    }

    private void zipInteractions(
            List<String> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(interactionsZipFormat(interactions), namePrefix + interactionsPostfix, zipOutputStream);
        else
            logger.error("Failed to archive not exist Interactions data.");
    }

    private String interactionsZipFormat(List<String> interactions) {
        return interactions.stream()
                .map(line -> line + System.lineSeparator())
                .collect(Collectors.joining());
    }

    private void zipStructuralElement(
            StructuralElement structuralElement,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (structuralElement != null) {
            List<String> stems = structuralElement.getStems();
            List<String> loops = structuralElement.getLoops();
            List<String> singleStrands = structuralElement.getSingleStrands();
            List<String> singleStrands5p = structuralElement.getSingleStrands5p();
            List<String> singleStrands3p = structuralElement.getSingleStrands3p();
            String coordinates = structuralElement.getCoordinates();

            if (stems != null && !stems.isEmpty() ||
                    loops != null && !loops.isEmpty() ||
                    singleStrands != null && !singleStrands.isEmpty() ||
                    singleStrands3p != null && !singleStrands3p.isEmpty() ||
                    singleStrands5p != null && !singleStrands5p.isEmpty())

                zipData(structuralElementsZipFormat(stems, loops, singleStrands, singleStrands5p, singleStrands3p),
                        namePrefix + structuralElementsPostfix,
                        zipOutputStream);

            if (coordinates != null)
                zipData(coordinates, namePrefix + coordinatesPostfix, zipOutputStream);
        } else
            logger.error("Failed to archive not exist Strands data.");
    }

    private String structuralElementsZipFormat(
            List<String> stems,
            List<String> loops,
            List<String> singleStrands,
            List<String> singleStrands5p,
            List<String> singleStrands3p) {
        List<String> stringList = new ArrayList<>();

        if (stems != null && !stems.isEmpty())
            stringList.add(stems.stream()
                    .map(line -> "Stem " + line + System.lineSeparator())
                    .collect(Collectors.joining()));

        if (loops != null && !loops.isEmpty())
            stringList.add(loops.stream()
                    .map(line -> "Loop " + line + System.lineSeparator())
                    .collect(Collectors.joining()));

        if (singleStrands != null && !singleStrands.isEmpty())
            stringList.add(singleStrands.stream()
                    .map(line -> "Single strand " + line + System.lineSeparator())
                    .collect(Collectors.joining()));

        if (singleStrands5p != null && !singleStrands5p.isEmpty())
            stringList.add(singleStrands5p.stream()
                    .map(line -> "Single strand 5' " + line + System.lineSeparator())
                    .collect(Collectors.joining()));

        if (singleStrands3p != null && !singleStrands3p.isEmpty())
            stringList.add(singleStrands3p.stream()
                    .map(line -> "Single strand 3' " + line + System.lineSeparator())
                    .collect(Collectors.joining()));

        return stringList.stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
