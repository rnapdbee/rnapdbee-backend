package pl.poznan.put.rnapdbee.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.rnapdbee.backend.model.Output2D;
import pl.poznan.put.rnapdbee.backend.model.Output3D;
import pl.poznan.put.rnapdbee.backend.model.Payload2DToThreeDots;
import pl.poznan.put.rnapdbee.backend.model.Payload3DThreeDots;

@RestController
@RequestMapping("api/rnapdbee/2d")
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