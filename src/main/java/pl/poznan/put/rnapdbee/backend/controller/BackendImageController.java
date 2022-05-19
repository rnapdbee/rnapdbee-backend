package pl.poznan.put.rnapdbee.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.rnapdbee.backend.model.*;

@RestController
@RequestMapping("api/rnapdbee/image")
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