package pl.poznan.put.rnapdbee.backend.downloadResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection2D;
import pl.poznan.put.rnapdbee.backend.images.ImageComponent;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.SecondaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.SingleStrand;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.StructuralElement;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.TertiaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.TertiaryToMultiSecondaryService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
    private static final String imagePostfix = "-2D";
    private static final String imageExtension = ".svg";

    private final SecondaryToDotBracketService secondaryToDotBracketService;
    private final TertiaryToDotBracketService tertiaryToDotBracketService;
    private final TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService;
    private final ImageComponent imageComponent;

    public DownloadResultService(
            SecondaryToDotBracketService secondaryToDotBracketService,
            TertiaryToDotBracketService tertiaryToDotBracketService,
            TertiaryToMultiSecondaryService tertiaryToMultiSecondaryService,
            ImageComponent imageComponent) {
        this.secondaryToDotBracketService = secondaryToDotBracketService;
        this.tertiaryToDotBracketService = tertiaryToDotBracketService;
        this.tertiaryToMultiSecondaryService = tertiaryToMultiSecondaryService;
        this.imageComponent = imageComponent;
    }

    public String download2DResult(
            UUID id,
            List<DownloadSelection2D> downloadSelection2DList,
            ZipOutputStream stream
    ) throws IOException {
        SecondaryToDotBracketMongoEntity document2D = secondaryToDotBracketService.findDocument(id);

        List<Output2D<ImageInformationPath>> output2DList = document2D
                .getResults()
                .stream()
                .map(ResultEntity::getOutput)
                .collect(Collectors.toList());

        String fileName = secondaryToDotBracketService.removeFileExtension(document2D.getFilename(), true);
        String basicDirPrefix = fileName + File.separator;
        stream.putNextEntry(new ZipEntry(basicDirPrefix));

        for (int i = 0; i < output2DList.size(); i++) {
            DownloadSelection2D selection2D = downloadSelection2DList.get(i);
            if (selection2D.isStrands()
                    || selection2D.isBpSeq()
                    || selection2D.isCt()
                    || selection2D.isInteractions()
                    || selection2D.isStructuralElements()
                    || selection2D.isImageInformation()) {

                String dirPrefix = basicDirPrefix + i + File.separator;
                stream.putNextEntry(new ZipEntry(dirPrefix));

                String namePrefix = dirPrefix + fileName;

                zipOutput2D(output2DList.get(i), selection2D, stream, namePrefix);
            }
        }

        return String.format("RNApdbee-%s-%s.zip", fileName, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()));
    }

    private void zipOutput2D(
            final Output2D<ImageInformationPath> output2D,
            final DownloadSelection2D downloadSelection2D,
            final ZipOutputStream zipOutputStream,
            final String namePrefix
    ) {
        if (downloadSelection2D.isStrands()) {
            List<SingleStrand> strands = output2D.getStrands();

            if (strands != null && !strands.isEmpty()) {
                zipFile(strandsZipFormat(strands), namePrefix + strandsPostfix, zipOutputStream);
            } else {
                logger.error("Failed to archive not exist Strands data.");
            }
        }

        if (downloadSelection2D.isBpSeq()) {
            List<String> bpSeq = output2D.getBpSeq();

            if (bpSeq != null && !bpSeq.isEmpty()) {
                zipFile(bpSeqZipFormat(bpSeq), namePrefix + bpSeqPostfix, zipOutputStream);
            } else {
                logger.error("Failed to archive not exist BPSEQ data.");
            }
        }

        if (downloadSelection2D.isCt()) {
            List<String> ct = output2D.getCt();

            if (ct != null && !ct.isEmpty()) {
                zipFile(ctZipFormat(ct), namePrefix + ctPostfix, zipOutputStream);
            } else {
                logger.error("Failed to archive not exist CT data.");
            }
        }

        if (downloadSelection2D.isInteractions()) {
            List<String> interactions = output2D.getInteractions();

            if (interactions != null && !interactions.isEmpty()) {
                zipFile(interactionsZipFormat(interactions), namePrefix + interactionsPostfix, zipOutputStream);
            } else {
                logger.error("Failed to archive not exist Interactions data.");
            }
        }

        if (downloadSelection2D.isStructuralElements()) {
            StructuralElement structuralElement = output2D.getStructuralElements();

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
                        singleStrands5p != null && !singleStrands5p.isEmpty()) {
                    zipFile(structuralElementsZipFormat(stems, loops, singleStrands, singleStrands5p, singleStrands3p),
                            namePrefix + structuralElementsPostfix,
                            zipOutputStream);
                }

                if (coordinates != null) {
                    zipFile(coordinates, namePrefix + coordinatesPostfix, zipOutputStream);
                }
            } else {
                logger.error("Failed to archive not exist Strands data.");
            }
        }

        if (downloadSelection2D.isImageInformation() && output2D.getImageInformation().wasDrawn()) {
            FileSystemResource resource = imageComponent.findSvgImage(output2D.getImageInformation().getPathToSVGImage());
            try {
                zipOutputStream.putNextEntry(new ZipEntry(namePrefix + imagePostfix + imageExtension));
                zipOutputStream.write(resource.getInputStream().readAllBytes());
                zipOutputStream.closeEntry();
            } catch (final IOException e) {
                logger.error("Failed to add image file to ZIP archive", e);
            }
        }
    }

    private void zipFile(
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

    private String strandsZipFormat(List<SingleStrand> strands) {
        return strands.stream()
                .map(SingleStrand::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String bpSeqZipFormat(List<String> bpSeq) {
        return bpSeq.stream()
                .map(seq -> seq + System.lineSeparator())
                .collect(Collectors.joining());
    }

    private String ctZipFormat(List<String> ct) {
        return ct.stream()
                .map(line -> line + System.lineSeparator())
                .collect(Collectors.joining());
    }

    private String interactionsZipFormat(List<String> interactions) {
        return interactions.stream()
                .map(line -> line + System.lineSeparator())
                .collect(Collectors.joining());
    }

    private String structuralElementsZipFormat(
            List<String> stems,
            List<String> loops,
            List<String> singleStrands,
            List<String> singleStrands5p,
            List<String> singleStrands3p) {
        StringBuilder stringBuilder = new StringBuilder();

        if (stems != null && !stems.isEmpty())
            stringBuilder.append(stems.stream()
                            .map(line -> "Stem " + line + System.lineSeparator())
                            .collect(Collectors.joining()))
                    .append(System.lineSeparator());

        if (loops != null && !loops.isEmpty())
            stringBuilder.append(loops.stream()
                            .map(line -> "Loop " + line + System.lineSeparator())
                            .collect(Collectors.joining()))
                    .append(System.lineSeparator());

        if (singleStrands != null && !singleStrands.isEmpty())
            stringBuilder.append(singleStrands.stream()
                            .map(line -> "Single strand " + line + System.lineSeparator())
                            .collect(Collectors.joining()))
                    .append(System.lineSeparator());

        if (singleStrands5p != null && !singleStrands5p.isEmpty())
            stringBuilder.append(singleStrands5p.stream()
                            .map(line -> "Single strand 5' " + line + System.lineSeparator())
                            .collect(Collectors.joining()))
                    .append(System.lineSeparator());

        if (singleStrands3p != null && !singleStrands3p.isEmpty()) {
            stringBuilder.append(singleStrands3p.stream()
                    .map(line -> "Single strand 3' " + line + System.lineSeparator())
                    .collect(Collectors.joining()));
        }

        return stringBuilder.toString();
    }
}
