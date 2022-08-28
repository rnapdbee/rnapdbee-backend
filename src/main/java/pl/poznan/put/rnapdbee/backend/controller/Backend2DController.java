package pl.poznan.put.rnapdbee.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.rnapdbee.backend.model.Output2D;
import pl.poznan.put.rnapdbee.backend.model.Payload2DToThreeDots;

@RestController
@RequestMapping("api/v1/engine/2d")
public class Backend2DController {

    private static final Logger logger = LoggerFactory.getLogger(Backend2DController.class);

    @PostMapping(path = "/", produces = "application/json")
    public Output2D calculate2D(@RequestBody Payload2DToThreeDots payload2DToThreeDots) {
        return new Output2D();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public Output2D fetch2D(@PathVariable(name = "id") Integer id) {
        return new Output2D();
    }
}
