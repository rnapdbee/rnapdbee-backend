package pl.poznan.put.rnapdbee.backend.downloadResult.controller;

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

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(path = "/download")
public class DownloadResultController {

    protected static final Logger logger = LoggerFactory.getLogger(DownloadResultController.class);
    private final DownloadResultService downloadResultService;

    public DownloadResultController(DownloadResultService downloadResultService) {
        this.downloadResultService = downloadResultService;
    }

    @PostMapping(path = "/{id}", produces = "application/zip", consumes = "application/json")
    public byte[] download2D(
            @PathVariable("id") UUID id,
            @RequestBody List<DownloadSelection2D> downloadSelection2DList,
            HttpServletResponse response
    ) throws IOException {
        logger.info(String.format("Prepare ZIP output for id [%s]", id));
        response.setContentType("application/zip");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

//            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
        String directoryName = downloadResultService.download2DResult(id, downloadSelection2DList, zipOutputStream);
//            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
//                    .filename(directoryName)
//                    .build();
        response.setHeader(
                "Content-Disposition",
                String.format("attachment; filename=\"%s\"", directoryName));

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();

//        } catch (IOException e) {
//            logger.error("Failed to prepare ZIP output stream", e);
//        }
    }
}
