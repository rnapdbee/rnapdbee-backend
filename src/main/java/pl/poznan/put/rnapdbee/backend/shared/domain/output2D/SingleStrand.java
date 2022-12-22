package pl.poznan.put.rnapdbee.backend.shared.domain.output2D;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing single Strand.
 */
public class SingleStrand {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("sequence")
    private final String sequence;
    @JsonProperty("structure")
    private final String structure;

    private SingleStrand(
            String name,
            String sequence,
            String structure) {
        this.name = name;
        this.sequence = sequence;
        this.structure = structure;
    }
}
