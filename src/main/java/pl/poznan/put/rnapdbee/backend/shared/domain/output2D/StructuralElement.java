package pl.poznan.put.rnapdbee.backend.shared.domain.output2D;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO class representing single Structural Element
 */
public class StructuralElement {
    @JsonProperty("stems")
    private final List<String> stems;
    @JsonProperty("loops")
    private final List<String> loops;
    @JsonProperty("singleStrands")
    private final List<String> singleStrands;
    @JsonProperty("singleStrands5p")
    private final List<String> singleStrands5p;
    @JsonProperty("singleStrands3p")
    private final List<String> singleStrands3p;
    @JsonProperty(value = "coordinates", access = JsonProperty.Access.WRITE_ONLY)
    private final String coordinates;

    private StructuralElement(
            List<String> stems,
            List<String> loops,
            List<String> singleStrands,
            List<String> singleStrands5p,
            List<String> singleStrands3p,
            String coordinates) {
        this.stems = stems;
        this.loops = loops;
        this.singleStrands = singleStrands;
        this.singleStrands5p = singleStrands5p;
        this.singleStrands3p = singleStrands3p;
        this.coordinates = coordinates;
    }
}
