package pl.poznan.put.rnapdbee.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.rnapdbee.backend.model.OutputImage;
import pl.poznan.put.rnapdbee.backend.model.ThreeDotsToImagePayload;

@RestController
@RequestMapping("api/v1/engine/image")
public class BackendImageController {

    private static final Logger logger = LoggerFactory.getLogger(BackendImageController.class);

    @PostMapping(path = "/", produces = "application/json")
    public OutputImage calculateMulti(@RequestBody ThreeDotsToImagePayload threeDotsToImagePayload) {
        return new OutputImage();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public OutputImage fetchExistingMulti(@PathVariable(name = "id") Integer id) {
        return new OutputImage();
    }
}
