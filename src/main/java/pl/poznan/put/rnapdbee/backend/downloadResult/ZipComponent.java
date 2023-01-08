package pl.poznan.put.rnapdbee.backend.downloadResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection2D;
import pl.poznan.put.rnapdbee.backend.images.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.SingleStrand;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.StructuralElement;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationPath;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipComponent {

    private static final Logger logger = LoggerFactory.getLogger(ZipComponent.class);

    private static final String strandsPostfix = "-2D-dotbracket.txt";
    private static final String bpSeqPostfix = "-2D-bpseq.txt";
    private static final String ctPostfix = "-2D-ct.txt";
    private static final String interactionsPostfix = "-rna-to-rna.txt";
    private static final String structuralElementsPostfix = "-elements.txt";
    private static final String coordinatesPostfix = "-coordinates.txt";
    private static final String image2DPostfix = "-2D";
    private static final String imageConsensusPostfix = "-consensus";
    private static final String imageExtension = ".svg";

    private final ZipFormatComponent zipFormatComponent;
    private final ImageComponent imageComponent;

    public ZipComponent(
            ZipFormatComponent zipFormatComponent,
            ImageComponent imageComponent) {
        this.zipFormatComponent = zipFormatComponent;
        this.imageComponent = imageComponent;
    }

    public void zipOutput2D(
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

    public void zipConsensualVisualization(
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
            zipData(zipFormatComponent.strandsZipFormat(strands), namePrefix + strandsPostfix, zipOutputStream);
        else
            logger.error("Failed to archive not exist Strands data.");

    }

    private void zipBpSeq(
            List<String> bpSeq,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (bpSeq != null && !bpSeq.isEmpty())
            zipData(zipFormatComponent.bpSeqZipFormat(bpSeq), namePrefix + bpSeqPostfix, zipOutputStream);
        else
            logger.error("Failed to archive not exist BPSEQ data.");

    }

    private void zipCt(
            List<String> ct,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (ct != null && !ct.isEmpty())
            zipData(zipFormatComponent.ctZipFormat(ct), namePrefix + ctPostfix, zipOutputStream);
        else
            logger.error("Failed to archive not exist CT data.");
    }

    private void zipInteractions(
            List<String> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.interactionsZipFormat(interactions), namePrefix + interactionsPostfix, zipOutputStream);
        else
            logger.error("Failed to archive not exist Interactions data.");
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

                zipData(zipFormatComponent.structuralElementsZipFormat(stems, loops, singleStrands, singleStrands5p, singleStrands3p),
                        namePrefix + structuralElementsPostfix,
                        zipOutputStream);

            if (coordinates != null)
                zipData(coordinates, namePrefix + coordinatesPostfix, zipOutputStream);
        } else
            logger.error("Failed to archive not exist Strands data.");
    }
}
