package pl.poznan.put.rnapdbee.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.rnapdbee.backend.model.Output3D;
import pl.poznan.put.rnapdbee.backend.model.OutputMulti;
import pl.poznan.put.rnapdbee.backend.model.Payload3DToMulti2D;

@RestController
@RequestMapping("api/v1/engine/multi")
public class Backend3DMulti2DController {

    private static final Logger logger = LoggerFactory.getLogger(Backend3DMulti2DController.class);

    @PostMapping(path = "/", produces = "application/json")
    public OutputMulti calculateMulti(@RequestBody Payload3DToMulti2D payload3DToMulti2D) {
        return new OutputMulti();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public OutputMulti fetchExistingMulti(@PathVariable(name = "id") Integer id) {
        return new OutputMulti();
    }

    @PostMapping(path = "/pdb/{pdbId}", produces = "application/json")
    public OutputMulti calculatePDBMulti(@PathVariable(name = "pdbId") String pdbId) {
        return new OutputMulti();
    }
}
