package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.BasePair;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing base pair.
 */
public class BasePair {
    @JsonProperty("interactionType")
    String interactionType;
    @JsonProperty("saenger")
    Saenger saenger;
    @JsonProperty("leontisWesthof")
    LeontisWesthof leontisWesthof;
    @JsonProperty("bPh")
    BPh bPh;
    @JsonProperty("br")
    BR br;
    @JsonProperty("stackingTopology")
    StackingTopology stackingTopology;
    @JsonProperty("leftResidue")
    NamedResidue leftResidue;
    @JsonProperty("rightResidue")
    NamedResidue rightResidue;
}
