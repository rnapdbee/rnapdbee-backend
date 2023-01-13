package pl.poznan.put.rnapdbee.backend.downloadResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection2D;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection3D;
import pl.poznan.put.rnapdbee.backend.images.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.SingleStrand;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.StructuralElement;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.BasePair;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.SingleTertiaryModelOutput;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationPath;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
class ZipComponent {

    private static final Logger logger = LoggerFactory.getLogger(ZipComponent.class);

    private static final String strandsSuffix = "-2D.dbn";
    private static final String bpSeqSuffix = "-2D.bpseq";
    private static final String ctSuffix = "-2D.ct";
    private static final String interactionsSuffix = "-interstrand.txt";
    private static final String structuralElementsSuffix = "-elements.txt";
    private static final String coordinatesSuffix = "-coordinates.";
    private static final String messagesSuffix = "-processing.log";
    private static final String canonicalInteractionsSuffix = "-canonical.csv";
    private static final String nonCanonicalInteractionsSuffix = "-non-canonical.csv";
    private static final String interStrandInteractionsSuffix = "-interstrand.csv";
    private static final String stackingInteractionsSuffix = "-stacking.csv";
    private static final String basePhosphateInteractionsSuffix = "-base-phosphate.csv";
    private static final String baseRiboseInteractionsSuffix = "-base-ribose.csv";

    private static final String image2DSuffix = "-2D";
    private static final String imageConsensusSuffix = "-consensus";
    private static final String imageExtension = ".svg";

    private final ZipFormatComponent zipFormatComponent;
    private final ImageComponent imageComponent;

    public ZipComponent(
            ZipFormatComponent zipFormatComponent,
            ImageComponent imageComponent) {
        this.zipFormatComponent = zipFormatComponent;
        this.imageComponent = imageComponent;
    }

    public void zipSingleTertiaryModelOutput(
            final SingleTertiaryModelOutput<ImageInformationPath> singleTertiaryModelOutput,
            final DownloadSelection3D.SingleDownloadSelection3D downloadSelection3D,
            final String namePrefix,
            final String analyzedFileExtension,
            final ZipOutputStream zipOutputStream
    ) {
        zipOutput2D(singleTertiaryModelOutput.getOutput2D(),
                downloadSelection3D.getOutput2D(),
                namePrefix,
                analyzedFileExtension,
                zipOutputStream);

        if (downloadSelection3D.isMessages())
            zipMessages(singleTertiaryModelOutput.getMessages(), namePrefix, zipOutputStream);

        if (downloadSelection3D.isCanonicalInteractions())
            zipCanonicalInteractions(singleTertiaryModelOutput.getCanonicalInteractions(), namePrefix, zipOutputStream);

        if (downloadSelection3D.isNonCanonicalInteractions())
            zipNonCanonicalInteractions(singleTertiaryModelOutput.getNonCanonicalInteractions(), namePrefix, zipOutputStream);

        if (downloadSelection3D.isInterStrandInteractions())
            zipInterStrandInteractions(singleTertiaryModelOutput.getInterStrandInteractions(), namePrefix, zipOutputStream);

        if (downloadSelection3D.isStackingInteractions())
            zipStackingInteractions(singleTertiaryModelOutput.getStackingInteractions(), namePrefix, zipOutputStream);

        if (downloadSelection3D.isBasePhosphateInteractions())
            zipBasePhosphateInteractions(singleTertiaryModelOutput.getBasePhosphateInteractions(), namePrefix, zipOutputStream);

        if (downloadSelection3D.isBaseRiboseInteractions())
            zipBaseRiboseInteractions(singleTertiaryModelOutput.getBaseRiboseInteractions(), namePrefix, zipOutputStream);
    }

    public void zipOutput2D(
            final Output2D<ImageInformationPath> output2D,
            final DownloadSelection2D downloadSelection2D,
            final String namePrefix,
            final String analyzedFileExtension,
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
            zipStructuralElement(output2D.getStructuralElements(), namePrefix, analyzedFileExtension, zipOutputStream);

        if (downloadSelection2D.isImageInformation() && output2D.getImageInformation().wasDrawn())
            zipImage(output2D.getImageInformation().getPathToSVGImage(), namePrefix, image2DSuffix, zipOutputStream);
    }

    public void zipConsensualVisualization(
            final ConsensualVisualizationPath consensualVisualizationPath,
            final String namePrefix,
            final ZipOutputStream zipOutputStream
    ) {
        String pathToSVGImage = consensualVisualizationPath.getPathToSVGImage();
        if (pathToSVGImage != null && pathToSVGImage.isBlank())
            zipImage(pathToSVGImage, namePrefix, imageConsensusSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist Consensus Visualization image.");
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
            String nameSuffix,
            ZipOutputStream zipOutputStream
    ) {
        FileSystemResource resource = imageComponent.findSvgImage(pathToSVGImage);
        String imageName = namePrefix + nameSuffix + imageExtension;
        try {
            byte[] image = resource.getInputStream().readAllBytes();

            zipOutputStream.putNextEntry(new ZipEntry(imageName));
            zipOutputStream.write(image);
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
            zipData(zipFormatComponent.strandsZipFormat(strands), namePrefix + strandsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist Strands data.");

    }

    private void zipBpSeq(
            List<String> bpSeq,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (bpSeq != null && !bpSeq.isEmpty())
            zipData(zipFormatComponent.bpSeqZipFormat(bpSeq), namePrefix + bpSeqSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist BPSEQ data.");

    }

    private void zipCt(
            List<String> ct,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (ct != null && !ct.isEmpty())
            zipData(zipFormatComponent.ctZipFormat(ct), namePrefix + ctSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist CT data.");
    }

    private void zipInteractions(
            List<String> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.interactionsZipFormat(interactions), namePrefix + interactionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist Interactions data.");
    }

    private void zipStructuralElement(
            StructuralElement structuralElement,
            String namePrefix,
            String coordinatesExtension,
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
                    singleStrands5p != null && !singleStrands5p.isEmpty() ||
                    singleStrands3p != null && !singleStrands3p.isEmpty())

                zipData(zipFormatComponent.structuralElementsZipFormat(stems, loops, singleStrands, singleStrands5p, singleStrands3p),
                        namePrefix + structuralElementsSuffix,
                        zipOutputStream);

            if (coordinates != null)
                zipData(coordinates, namePrefix + coordinatesSuffix + coordinatesExtension, zipOutputStream);
        } else
            logger.error("Failed to archive not exist Strands data.");
    }

    private void zipMessages(
            List<String> messages,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (messages != null && !messages.isEmpty())
            zipData(zipFormatComponent.messagesZipFormat(messages), namePrefix + messagesSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist Messages data.");
    }

    private void zipCanonicalInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.canonicalBasePairsToCSV(interactions), namePrefix + canonicalInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist CanonicalInteractions data.");
    }

    private void zipNonCanonicalInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + nonCanonicalInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist NonCanonicalInteractions data.");
    }

    private void zipInterStrandInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + interStrandInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist InterStrandInteractions data.");
    }

    private void zipStackingInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + stackingInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist StackingInteractions data.");
    }

    private void zipBasePhosphateInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + basePhosphateInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist BasePhosphateInteractions data.");
    }

    private void zipBaseRiboseInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + baseRiboseInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not exist BaseRiboseInteractions data.");
    }
}
