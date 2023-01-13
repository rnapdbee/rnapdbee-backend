package pl.poznan.put.rnapdbee.backend.downloadResult.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.rnapdbee.backend.downloadResult.DownloadResultService;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection2D;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelection3D;
import pl.poznan.put.rnapdbee.backend.downloadResult.domain.DownloadSelectionMulti;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

/**
 * Controller class responsible for downloading zipped results from a calculation
 */
@RestController
@RequestMapping(path = "api/v1/engine/download")
public class DownloadResultController {

    protected static final Logger logger = LoggerFactory.getLogger(DownloadResultController.class);
    private final DownloadResultService downloadResultService;

    public DownloadResultController(DownloadResultService downloadResultService) {
        this.downloadResultService = downloadResultService;
    }

    @Operation(summary = "Get zipped content of the selected elements from 3D -> (...) analysis")
    @PostMapping(path = "/3d/{id}", produces = "application/zip", consumes = "application/json")
    public byte[] download3D(
            @PathVariable("id") UUID id,
            @RequestBody List<DownloadSelection3D> downloadSelection3DList,
            HttpServletResponse response
    ) throws IOException {
        logger.info(String.format("Prepare 3D scenario ZIP results for id [%s]", id));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        String zipName = downloadResultService.download3DResults(id, downloadSelection3DList, zipOutputStream);

        zipOutputStream.finish();
        zipOutputStream.flush();

        IOUtils.closeQuietly(zipOutputStream);
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", zipName));

        return byteArrayOutputStream.toByteArray();
    }

    @Operation(summary = "Get zipped content of the selected elements from 2D -> (...) analysis")
    @PostMapping(path = "/2d/{id}", produces = "application/zip", consumes = "application/json")
    public byte[] download2D(
            @PathVariable("id") UUID id,
            @RequestBody List<DownloadSelection2D> downloadSelection2DList,
            HttpServletResponse response
    ) throws IOException {
        logger.info(String.format("Prepare 2D scenario ZIP results for id [%s]", id));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        String zipName = downloadResultService.download2DResults(id, downloadSelection2DList, zipOutputStream);

        zipOutputStream.finish();
        zipOutputStream.flush();

        IOUtils.closeQuietly(zipOutputStream);
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", zipName));

        return byteArrayOutputStream.toByteArray();
    }

    @Operation(summary = "Get zipped content of the selected elements from 3D -> multi 2D analysis")
    @PostMapping(path = "/multi/{id}", produces = "application/zip", consumes = "application/json")
    public byte[] downloadMulti(
            @PathVariable("id") UUID id,
            @RequestBody List<DownloadSelectionMulti> downloadSelectionMultiList,
            HttpServletResponse response
    ) throws IOException {
        logger.info(String.format("Prepare Multi scenario ZIP results for id [%s]", id));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        String zipName = downloadResultService.downloadMultiResults(id, downloadSelectionMultiList, zipOutputStream);

        zipOutputStream.finish();
        zipOutputStream.flush();

        IOUtils.closeQuietly(zipOutputStream);
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", zipName));

        return byteArrayOutputStream.toByteArray();
    }
}
