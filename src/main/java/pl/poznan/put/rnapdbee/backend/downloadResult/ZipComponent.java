package pl.poznan.put.rnapdbee.backend.downloadResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
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
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
class ZipComponent {

    private static final Logger logger = LoggerFactory.getLogger(ZipComponent.class);

    private static final String RUN_COMMAND_PATH = "/run-command";
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
    private final RestTemplate restTemplate;
    private final String svgToPdfServiceUrl;

    public ZipComponent(
            ZipFormatComponent zipFormatComponent,
            ImageComponent imageComponent,
            @Value("${svg-to-pdf.service.url}") String svgToPdfServiceUrl) {
        this.zipFormatComponent = zipFormatComponent;
        this.imageComponent = imageComponent;
        this.restTemplate = new RestTemplate();
        this.svgToPdfServiceUrl = svgToPdfServiceUrl;
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
        if (pathToSVGImage != null && !pathToSVGImage.isBlank())
            zipImage(pathToSVGImage, namePrefix, imageConsensusSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing Consensus Visualization image.");
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
        String imageNamePrefix = namePrefix + nameSuffix;
        String svgImageName = imageNamePrefix + imageExtension;
        try {
            byte[] svgImage = resource.getInputStream().readAllBytes();

            zipOutputStream.putNextEntry(new ZipEntry(svgImageName));
            zipOutputStream.write(svgImage);
            zipOutputStream.closeEntry();

            byte[] pdfImage = convertSvgToPdf(svgImage, imageNamePrefix);
            if (pdfImage != null) {
                String pdfImageName = imageNamePrefix + ".pdf";
                zipOutputStream.putNextEntry(new ZipEntry(pdfImageName));
                zipOutputStream.write(pdfImage);
                zipOutputStream.closeEntry();
            }
        } catch (IOException e) {
            logger.error(String.format("Failed to add [%s] image file to ZIP archive", svgImageName), e);
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
            logger.error("Failed to archive not existing Strands data.");

    }

    private void zipBpSeq(
            List<String> bpSeq,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (bpSeq != null && !bpSeq.isEmpty())
            zipData(zipFormatComponent.bpSeqZipFormat(bpSeq), namePrefix + bpSeqSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing BPSEQ data.");

    }

    private void zipCt(
            List<String> ct,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (ct != null && !ct.isEmpty())
            zipData(zipFormatComponent.ctZipFormat(ct), namePrefix + ctSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing CT data.");
    }

    private void zipInteractions(
            List<String> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.interactionsZipFormat(interactions), namePrefix + interactionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing Interactions data.");
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
            logger.error("Failed to archive not existing Strands data.");
    }

    private void zipMessages(
            List<String> messages,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (messages != null && !messages.isEmpty())
            zipData(zipFormatComponent.messagesZipFormat(messages), namePrefix + messagesSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing Messages data.");
    }

    private void zipCanonicalInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.canonicalBasePairsToCSV(interactions), namePrefix + canonicalInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing CanonicalInteractions data.");
    }

    private void zipNonCanonicalInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + nonCanonicalInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing NonCanonicalInteractions data.");
    }

    private void zipInterStrandInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + interStrandInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing InterStrandInteractions data.");
    }

    private void zipStackingInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + stackingInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing StackingInteractions data.");
    }

    private void zipBasePhosphateInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + basePhosphateInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing BasePhosphateInteractions data.");
    }

    private void zipBaseRiboseInteractions(
            List<BasePair> interactions,
            String namePrefix,
            ZipOutputStream zipOutputStream
    ) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + baseRiboseInteractionsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing BaseRiboseInteractions data.");
    }

    private byte[] convertSvgToPdf(byte[] svgData, String caption) {
        try {
            // Prepare request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("arguments", "python");
            body.add("arguments", "/add-text-to-svg.py");
            body.add("arguments", "input.svg");
            body.add("arguments", caption);
            body.add("arguments", "output.pdf");
            body.add("output_files", "output.pdf");

            ByteArrayResource fileResource =
                    new ByteArrayResource(svgData) {
                        @Override
                        public String getFilename() {
                            return "input.svg";
                        }
                    };
            body.add("input_files", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Execute request
            String url = svgToPdfServiceUrl + RUN_COMMAND_PATH;
            logger.debug("Sending svg->pdf conversion request to service at: {}", url);

            Map<String, Object> response = restTemplate.postForObject(url, requestEntity, Map.class);

            // Process response
            if (response != null) {
                logger.debug("Received response from svg->pdf conversion service");

                // Log stdout and stderr
                if (response.containsKey("stdout")) {
                    logger.debug("svg->pdf stdout: {}", response.get("stdout"));
                }

                if (response.containsKey("stderr")) {
                    String stderr = (String) response.get("stderr");
                    if (stderr != null && !stderr.isEmpty()) {
                        logger.warn("svg->pdf stderr: {}", stderr);
                    }
                }

                // Check exit code
                Integer exitCode = (Integer) response.get("exit_code");
                if (exitCode != null && exitCode != 0) {
                    logger.error("svg->pdf conversion command failed with exit code: {}", exitCode);
                    return null;
                }

                // Process output files
                if (response.containsKey("output_files") && response.get("output_files") != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> outputFiles =
                            (List<Map<String, String>>) response.get("output_files");

                    for (Map<String, String> file : outputFiles) {
                        String relativePath = file.get("relative_path");
                        if ("output.pdf".equals(relativePath) && file.containsKey("content_base64")) {
                            return Base64.getDecoder().decode(file.get("content_base64"));
                        }
                    }
                }

                logger.warn("No output.pdf file found in the response for conversion");
            } else {
                logger.error("Received null response from svg->pdf conversion service");
            }
        } catch (RestClientException e) {
            logger.error("Error communicating with svg->pdf conversion service", e);
        } catch (Exception e) {
            logger.error("Unexpected error during svg->pdf conversion", e);
        }

        return null;
    }
}
