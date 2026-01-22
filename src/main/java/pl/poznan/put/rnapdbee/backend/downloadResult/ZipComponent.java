package pl.poznan.put.rnapdbee.backend.downloadResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection2D;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection3D;
import pl.poznan.put.rnapdbee.backend.images.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.SingleStrand;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.StructuralElement;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.BasePair;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.BaseTriple;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.SingleTertiaryModelOutput;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationPath;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private static final String baseTriplesSuffix = "-base-triples.csv";

    private static final String image2DSuffix = "-2D";
    private static final String imageConsensusSuffix = "-consensus";
    private static final String imageExtension = ".svg";

    private final ZipFormatComponent zipFormatComponent;
    private final ImageComponent imageComponent;
    private final RestTemplate restTemplate;
    private final String svgToPdfServiceUrl;
    private final ObjectMapper objectMapper;

    public ZipComponent(ZipFormatComponent zipFormatComponent, ImageComponent imageComponent,
            @Value("${svg-to-pdf.service.url}") String svgToPdfServiceUrl) {
        this.zipFormatComponent = zipFormatComponent;
        this.imageComponent = imageComponent;
        this.restTemplate = new RestTemplate();
        this.svgToPdfServiceUrl = svgToPdfServiceUrl;
        this.objectMapper = new ObjectMapper();
    }

    public void zipSingleTertiaryModelOutput(
            final SingleTertiaryModelOutput<ImageInformationPath> singleTertiaryModelOutput,
            final DownloadSelection3D.SingleDownloadSelection3D downloadSelection3D, final String namePrefix,
            final String analyzedFileExtension, final ZipOutputStream zipOutputStream) {
        zipOutput2D(singleTertiaryModelOutput.getOutput2D(), downloadSelection3D.getOutput2D(), namePrefix,
                analyzedFileExtension, zipOutputStream);

        if (downloadSelection3D.isMessages())
            zipMessages(singleTertiaryModelOutput.getMessages(), namePrefix, zipOutputStream);

        if (downloadSelection3D.isCanonicalInteractions())
            zipCanonicalInteractions(singleTertiaryModelOutput.getCanonicalInteractions(), namePrefix, zipOutputStream);

        if (downloadSelection3D.isNonCanonicalInteractions())
            zipNonCanonicalInteractions(singleTertiaryModelOutput.getNonCanonicalInteractions(), namePrefix,
                    zipOutputStream);

        if (downloadSelection3D.isInterStrandInteractions())
            zipInterStrandInteractions(singleTertiaryModelOutput.getInterStrandInteractions(), namePrefix,
                    zipOutputStream);

        if (downloadSelection3D.isStackingInteractions())
            zipStackingInteractions(singleTertiaryModelOutput.getStackingInteractions(), namePrefix, zipOutputStream);

        if (downloadSelection3D.isBasePhosphateInteractions())
            zipBasePhosphateInteractions(singleTertiaryModelOutput.getBasePhosphateInteractions(), namePrefix,
                    zipOutputStream);

        if (downloadSelection3D.isBaseRiboseInteractions())
            zipBaseRiboseInteractions(singleTertiaryModelOutput.getBaseRiboseInteractions(), namePrefix,
                    zipOutputStream);

        if (downloadSelection3D.isBaseTriples())
            zipBaseTriples(singleTertiaryModelOutput.getBaseTriples(), namePrefix, zipOutputStream);
    }

    public void zipOutput2D(final Output2D<ImageInformationPath> output2D,
            final DownloadSelection2D downloadSelection2D, final String namePrefix, final String analyzedFileExtension,
            final ZipOutputStream zipOutputStream) {
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

    public void zipConsensualVisualization(final ConsensualVisualizationPath consensualVisualizationPath,
            final String namePrefix, final ZipOutputStream zipOutputStream) {
        String pathToSVGImage = consensualVisualizationPath.getPathToSVGImage();
        if (pathToSVGImage != null && !pathToSVGImage.isBlank())
            zipImage(pathToSVGImage, namePrefix, imageConsensusSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing Consensus Visualization image.");
    }

    private void zipData(String data, String filename, ZipOutputStream zipOutputStream) {
        try {
            zipOutputStream.putNextEntry(new ZipEntry(filename));
            IOUtils.write(data, zipOutputStream, Charset.defaultCharset());
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            logger.error(String.format("Failed to add [%s] file to ZIP archive.", filename), e);
        }
    }

    private void zipImage(String pathToSVGImage, String namePrefix, String nameSuffix,
            ZipOutputStream zipOutputStream) {
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

    private void zipStrands(List<SingleStrand> strands, String namePrefix, ZipOutputStream zipOutputStream) {
        if (strands != null && !strands.isEmpty())
            zipData(zipFormatComponent.strandsZipFormat(strands), namePrefix + strandsSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing Strands data.");

    }

    private void zipBpSeq(List<String> bpSeq, String namePrefix, ZipOutputStream zipOutputStream) {
        if (bpSeq != null && !bpSeq.isEmpty())
            zipData(zipFormatComponent.bpSeqZipFormat(bpSeq), namePrefix + bpSeqSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing BPSEQ data.");

    }

    private void zipCt(List<String> ct, String namePrefix, ZipOutputStream zipOutputStream) {
        if (ct != null && !ct.isEmpty())
            zipData(zipFormatComponent.ctZipFormat(ct), namePrefix + ctSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing CT data.");
    }

    private void zipInteractions(List<String> interactions, String namePrefix, ZipOutputStream zipOutputStream) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.interactionsZipFormat(interactions), namePrefix + interactionsSuffix,
                    zipOutputStream);
        else
            logger.error("Failed to archive not existing Interactions data.");
    }

    private void zipStructuralElement(StructuralElement structuralElement, String namePrefix,
            String coordinatesExtension, ZipOutputStream zipOutputStream) {
        if (structuralElement != null) {
            List<String> stems = structuralElement.getStems();
            List<String> loops = structuralElement.getLoops();
            List<String> singleStrands = structuralElement.getSingleStrands();
            List<String> singleStrands5p = structuralElement.getSingleStrands5p();
            List<String> singleStrands3p = structuralElement.getSingleStrands3p();
            String coordinates = structuralElement.getCoordinates();

            if (stems != null && !stems.isEmpty() || loops != null && !loops.isEmpty()
                    || singleStrands != null && !singleStrands.isEmpty()
                    || singleStrands5p != null && !singleStrands5p.isEmpty()
                    || singleStrands3p != null && !singleStrands3p.isEmpty())

                zipData(zipFormatComponent.structuralElementsZipFormat(stems, loops, singleStrands, singleStrands5p,
                        singleStrands3p), namePrefix + structuralElementsSuffix, zipOutputStream);

            if (coordinates != null)
                zipData(coordinates, namePrefix + coordinatesSuffix + coordinatesExtension, zipOutputStream);
        } else
            logger.error("Failed to archive not existing Strands data.");
    }

    private void zipMessages(List<String> messages, String namePrefix, ZipOutputStream zipOutputStream) {
        if (messages != null && !messages.isEmpty())
            zipData(zipFormatComponent.messagesZipFormat(messages), namePrefix + messagesSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing Messages data.");
    }

    private void zipCanonicalInteractions(List<BasePair> interactions, String namePrefix,
            ZipOutputStream zipOutputStream) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.canonicalBasePairsToCSV(interactions), namePrefix + canonicalInteractionsSuffix,
                    zipOutputStream);
        else
            logger.error("Failed to archive not existing CanonicalInteractions data.");
    }

    private void zipNonCanonicalInteractions(List<BasePair> interactions, String namePrefix,
            ZipOutputStream zipOutputStream) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + nonCanonicalInteractionsSuffix,
                    zipOutputStream);
        else
            logger.error("Failed to archive not existing NonCanonicalInteractions data.");
    }

    private void zipInterStrandInteractions(List<BasePair> interactions, String namePrefix,
            ZipOutputStream zipOutputStream) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + interStrandInteractionsSuffix,
                    zipOutputStream);
        else
            logger.error("Failed to archive not existing InterStrandInteractions data.");
    }

    private void zipStackingInteractions(List<BasePair> interactions, String namePrefix,
            ZipOutputStream zipOutputStream) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + stackingInteractionsSuffix,
                    zipOutputStream);
        else
            logger.error("Failed to archive not existing StackingInteractions data.");
    }

    private void zipBasePhosphateInteractions(List<BasePair> interactions, String namePrefix,
            ZipOutputStream zipOutputStream) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + basePhosphateInteractionsSuffix,
                    zipOutputStream);
        else
            logger.error("Failed to archive not existing BasePhosphateInteractions data.");
    }

    private void zipBaseRiboseInteractions(List<BasePair> interactions, String namePrefix,
            ZipOutputStream zipOutputStream) {
        if (interactions != null && !interactions.isEmpty())
            zipData(zipFormatComponent.basePairsToCSV(interactions), namePrefix + baseRiboseInteractionsSuffix,
                    zipOutputStream);
        else
            logger.error("Failed to archive not existing BaseRiboseInteractions data.");
    }

    private void zipBaseTriples(List<BaseTriple> baseTriples, String namePrefix, ZipOutputStream zipOutputStream) {
        if (baseTriples != null && !baseTriples.isEmpty())
            zipData(zipFormatComponent.baseTriplesToCSV(baseTriples), namePrefix + baseTriplesSuffix, zipOutputStream);
        else
            logger.error("Failed to archive not existing BaseTriples data.");
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

            ByteArrayResource fileResource = new ByteArrayResource(svgData) {
                @Override
                public String getFilename() {
                    return "input.svg";
                }
            };
            body.add("input_files", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Execute request
            String url = UriComponentsBuilder.fromHttpUrl(svgToPdfServiceUrl)
                    .path(RUN_COMMAND_PATH)
                    .toUriString();
            logger.debug("Sending svg->pdf conversion request to service at: {}", url);

            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class);

            MediaType contentType = responseEntity.getHeaders().getContentType();
            if (contentType != null && MediaType.MULTIPART_FORM_DATA.includes(contentType)) {
                String boundary = extractBoundary(contentType);
                if (boundary == null) {
                    logger.error("Multipart svg->pdf response missing boundary parameter");
                    return null;
                }
                return handleMultipartResponse(responseEntity.getBody(), boundary);
            }

            Map<String, Object> response = parseJsonResponse(responseEntity.getBody());
            return handleLegacyResponse(response);
        } catch (RestClientException e) {
            logger.error("Error communicating with svg->pdf conversion service", e);
        } catch (Exception e) {
            logger.error("Unexpected error during svg->pdf conversion", e);
        }

        return null;
    }

    private byte[] handleMultipartResponse(byte[] responseBody, String boundary) throws IOException {
        if (responseBody == null || responseBody.length == 0) {
            logger.error("Received empty multipart response from svg->pdf conversion service");
            return null;
        }

        List<MultipartPart> parts = parseMultipartParts(responseBody, boundary);
        Map<String, Object> metadata = parseMetadataPart(parts);
        logMetadata(metadata);
        if (!validateExitCode(metadata)) {
            return null;
        }

        byte[] pdfBytes = extractFileBytes(parts, "output.pdf");
        if (pdfBytes == null) {
            logger.warn("No output.pdf file found in the multipart response");
        }
        return pdfBytes;
    }

    private byte[] handleLegacyResponse(Map<String, Object> response) {
        if (response == null) {
            logger.error("Received null response from svg->pdf conversion service");
            return null;
        }

        logger.debug("Received response from svg->pdf conversion service");
        logMetadata(response);
        if (!validateExitCode(response)) {
            return null;
        }

        if (response.containsKey("output_files") && response.get("output_files") != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> outputFiles = (List<Map<String, String>>) response.get("output_files");

            for (Map<String, String> file : outputFiles) {
                String relativePath = file.get("relative_path");
                if ("output.pdf".equals(relativePath) && file.containsKey("content_base64")) {
                    return Base64.getDecoder().decode(file.get("content_base64"));
                }
            }
        }

        logger.warn("No output.pdf file found in the response for conversion");
        return null;
    }

    private Map<String, Object> parseMetadataPart(List<MultipartPart> parts) throws IOException {
        for (MultipartPart part : parts) {
            if ("metadata".equals(part.getName())) {
                return objectMapper.readValue(part.getContent(), new TypeReference<Map<String, Object>>() {
                });
            }
        }

        return null;
    }

    private Map<String, Object> parseJsonResponse(byte[] responseBody) throws IOException {
        if (responseBody == null || responseBody.length == 0) {
            return null;
        }

        return objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
        });
    }

    private void logMetadata(Map<String, Object> metadata) {
        if (metadata == null) {
            return;
        }

        if (metadata.containsKey("stdout")) {
            logger.debug("svg->pdf stdout: {}", metadata.get("stdout"));
        }

        if (metadata.containsKey("stderr")) {
            String stderr = String.valueOf(metadata.get("stderr"));
            if (stderr != null && !stderr.isEmpty() && !"null".equals(stderr)) {
                logger.warn("svg->pdf stderr: {}", stderr);
            }
        }
    }

    private boolean validateExitCode(Map<String, Object> metadata) {
        if (metadata == null || !metadata.containsKey("exit_code")) {
            return true;
        }

        Integer exitCode = null;
        Object exitValue = metadata.get("exit_code");
        if (exitValue instanceof Number) {
            exitCode = ((Number) exitValue).intValue();
        } else if (exitValue != null) {
            try {
                exitCode = Integer.parseInt(exitValue.toString());
            } catch (NumberFormatException ignored) {
                logger.warn("Unable to parse svg->pdf exit_code: {}", exitValue);
            }
        }

        if (exitCode != null && exitCode != 0) {
            logger.error("svg->pdf conversion command failed with exit code: {}", exitCode);
            return false;
        }
        return true;
    }

    private byte[] extractFileBytes(List<MultipartPart> parts, String expectedFilename) {
        for (MultipartPart part : parts) {
            if (expectedFilename.equals(part.getFilename())) {
                return part.getContent();
            }
        }
        return null;
    }

    private List<MultipartPart> parseMultipartParts(byte[] responseBody, String boundary) {
        String rawBody = new String(responseBody, StandardCharsets.ISO_8859_1);
        String boundaryMarker = "--" + boundary;
        String[] sections = rawBody.split(Pattern.quote(boundaryMarker));

        List<MultipartPart> parts = new java.util.ArrayList<>();
        for (String section : sections) {
            String trimmed = trimSection(section);
            if (trimmed.isEmpty() || "--".equals(trimmed)) {
                continue;
            }

            int headerEndIndex = trimmed.indexOf("\r\n\r\n");
            if (headerEndIndex < 0) {
                continue;
            }

            String headerBlock = trimmed.substring(0, headerEndIndex);
            String bodyBlock = trimmed.substring(headerEndIndex + 4);
            if (bodyBlock.endsWith("\r\n")) {
                bodyBlock = bodyBlock.substring(0, bodyBlock.length() - 2);
            }

            MultipartHeaders headers = parseHeaders(headerBlock);
            byte[] content = bodyBlock.getBytes(StandardCharsets.ISO_8859_1);
            parts.add(new MultipartPart(headers.name, headers.filename, content));
        }

        if (parts.isEmpty()) {
            logger.warn("No parts parsed from multipart response");
        }
        return parts;
    }

    private String trimSection(String section) {
        String trimmed = section;
        if (trimmed.startsWith("\r\n")) {
            trimmed = trimmed.substring(2);
        }
        if (trimmed.endsWith("--")) {
            trimmed = trimmed.substring(0, trimmed.length() - 2);
        }
        return trimmed;
    }

    private MultipartHeaders parseHeaders(String headerBlock) {
        String[] lines = headerBlock.split("\r\n");
        String name = null;
        String filename = null;
        for (String line : lines) {
            int colonIndex = line.indexOf(':');
            if (colonIndex < 0) {
                continue;
            }
            String headerName = line.substring(0, colonIndex).trim();
            String headerValue = line.substring(colonIndex + 1).trim();
            if ("Content-Disposition".equalsIgnoreCase(headerName)) {
                name = extractDispositionValue(headerValue, "name");
                filename = extractDispositionValue(headerValue, "filename");
            }
        }
        return new MultipartHeaders(name, filename);
    }

    private String extractDispositionValue(String headerValue, String attribute) {
        Pattern pattern = Pattern.compile(attribute + "=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(headerValue);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractBoundary(MediaType contentType) {
        String boundary = contentType.getParameter("boundary");
        if (boundary == null) {
            return null;
        }
        if (boundary.startsWith("\"") && boundary.endsWith("\"") && boundary.length() > 1) {
            return boundary.substring(1, boundary.length() - 1);
        }
        return boundary;
    }

    private static final class MultipartHeaders {
        private final String name;
        private final String filename;

        private MultipartHeaders(String name, String filename) {
            this.name = name;
            this.filename = filename;
        }
    }

    private static final class MultipartPart {
        private final String name;
        private final String filename;
        private final byte[] content;

        private MultipartPart(String name, String filename, byte[] content) {
            this.name = name;
            this.filename = filename;
            this.content = content;
        }

        private String getName() {
            return name;
        }

        private String getFilename() {
            return filename;
        }

        private byte[] getContent() {
            return content;
        }
    }
}
