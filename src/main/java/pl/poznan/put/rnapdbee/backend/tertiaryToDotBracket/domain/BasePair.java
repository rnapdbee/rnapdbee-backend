package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing base pair.
 */
public class BasePair {
    @JsonProperty("interactionType")
    String interactionType;
    @JsonProperty("saenger")
    String saenger;
    @JsonProperty("leontisWesthof")
    String leontisWesthof;
    @JsonProperty("bPh")
    String bPh;
    @JsonProperty("br")
    String br;
    @JsonProperty("stackingTopology")
    String stackingTopology;
    @JsonProperty("leftResidue")
    NamedResidue leftResidue;
    @JsonProperty("rightResidue")
    NamedResidue rightResidue;
}
